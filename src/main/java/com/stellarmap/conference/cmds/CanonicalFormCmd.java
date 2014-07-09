package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceCommand;
import com.stellarmap.conference.ConferenceController;
import com.stellarmap.cs.CanonicalForm;
import com.stellarmap.cs.CanonicalStore;
import com.stellarmap.cs.ICanonicalStore;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * Command to manage the canonical form from the canonical store.
 * Created by barclakj on 29/05/2014.
 */
public class CanonicalFormCmd implements ConferenceCommand {
    public static Logger log = Logger.getLogger(CanonicalFormCmd.class.getCanonicalName());
    public static final String DATA_KEY = "data";
    public static final String ASSOC_KEY = "assoc";

    private ICanonicalStore canonicalStore = new CanonicalStore();

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = null;

        JSONObject data = null;
        String reference = null;

        if (jsonObject.has(ASSOC_KEY)) {
            reference = jsonObject.getString(ASSOC_KEY);
        }

        if (jsonObject.has(DATA_KEY)) {
            data = jsonObject.getJSONObject(DATA_KEY);
            CanonicalForm canonForm = CanonicalStore.jsonObjectToCanonicalForm(data);
            if (canonForm!=null) {
                boolean changed = false;
                if (canonForm.getNewTs()!=canonForm.getTs()) {
                    changed = true;
                }

                if (canonicalStore.store(canonForm)) {
                    // if we've a reference assoc then set it.
                    if (reference!=null) {
                        canonicalStore.addItem(reference, canonForm.getReference());
                    }

                    // only reflect back if object changed.
                    if (changed) {
                        log.info("Canonical store ok! Let client know it's ok so can update timestamps.");
                        responseObject = createConfirmOKMessage(canonForm);
                    } else {
                        responseObject = null;
                    }
                } else {
                    log.info("Canonical store failed - returning reflection!");
                    responseObject = createReflectedVersion(canonForm);
                }
            } // nothing wrong if we can't convert it...
        } else {
            // create error message
            log.info("Canonical store failed - not valid!");
            responseObject = createNotValidErrorMessage();
        }

        return responseObject;
    }

    /**
     * Returns an error message as the content of the payload is not valid.
     * @return
     */
    public static JSONObject createNotValidErrorMessage() {
        JSONObject responseObject = new JSONObject();
        responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
        responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "Message payload not valid.");
        return responseObject;
    }

    /**
     * Creates a message containing the latest stored version of the object.
     * @return
     */
    public static JSONObject createReflectedVersion(CanonicalForm canonForm) {
        JSONObject responseObject = new JSONObject();

        responseObject.put(ConferenceController.CONF_COMMAND, ConferenceController.REFLECTION);

        JSONObject content = new JSONObject();
        content.put("object", canonForm.getData());
        content.put("action", "reflection");
        responseObject.put(ConferenceController.CONF_MSG, content);

        return responseObject;
    }

    /**
     * Creates message confirming all is ok. Current version you have is the same
     * as the store.
     * @return
     */
    public static JSONObject createConfirmOKMessage(CanonicalForm canonForm) {
        // only bother if the timestamps are different... else client already
        // has the correct value.
        if (canonForm.getNewTs()!=canonForm.getTs()) {
            JSONObject responseObject = new JSONObject();
            responseObject.put(ConferenceController.CONF_COMMAND, ConferenceController.REFLECTION);
            responseObject.put("action", "updateTimestamp");
            responseObject.put("ref", canonForm.getReference());
            responseObject.put("updTs", canonForm.getNewTs());
            return responseObject;
        } else {
            return null;
        }
    }
}
