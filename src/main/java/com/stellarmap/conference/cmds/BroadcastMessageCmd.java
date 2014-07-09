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
 * Created by barclakj on 29/05/2014.
 */
public class BroadcastMessageCmd implements ConferenceCommand {
    private static Logger log = Logger.getLogger(BroadcastMessageCmd.class.getCanonicalName());

    private ICanonicalStore canonicalStore = new CanonicalStore();


    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        boolean fwd = true;
        JSONObject responseObject = null;
        String reference = null;

        // obtain association if it exists.
        if (jsonObject.has("msg") && jsonObject.getJSONObject("msg").has(CanonicalFormCmd.ASSOC_KEY)) {
            reference = jsonObject.getJSONObject("msg").getString(CanonicalFormCmd.ASSOC_KEY);
        }

        // if broadcast then ensure canonical store is updated first if need be.
        if (jsonObject.has("msg") && jsonObject.getJSONObject("msg").has("object")) {
            JSONObject content = jsonObject.getJSONObject("msg").getJSONObject("object");
            CanonicalForm cf = CanonicalStore.jsonObjectToCanonicalForm(content);
            if (cf!=null) {
                if (!canonicalStore.store(cf)) {
                    // if not stored then don't pass the msg on and return the
                    // correct version to the client.
                    log.warning("Invalid version - reflecting back the correct version!");
                    responseObject = CanonicalFormCmd.createReflectedVersion(cf);
                    fwd = false;
                } else {
                    // send back confirmation to client.
                    // if we've a reference assoc then set it.
                    if (reference!=null) {
                        canonicalStore.addItem(reference, cf.getReference());
                    }
                    log.info("Valid version - confirming receipt.");
                    responseObject = CanonicalFormCmd.createConfirmOKMessage(cf);
                }
            }
        }

        if (fwd) {
            ConferenceController.placeMessage(jsonObject, clientInterface);
        }
        return responseObject;
    }
}
