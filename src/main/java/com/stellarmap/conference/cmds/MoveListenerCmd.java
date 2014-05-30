package com.stellarmap.conference.cmds;

import com.stellarmap.conference.Conference;
import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class MoveListenerCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        /* JSONObject responseObject = new JSONObject();

        String listener = jsonObject.getString(ConferenceController.CONF_LISTENER_CODE);
        String confCode = jsonObject.getString(ConferenceController.CONFERENCE_CODE);
        Conference conf = ConferenceManager.locateConference(confCode);
        if (listener!=null) {
            ConferenceManager.moveListener(clientInterface, confCode);
            conf.remove(listener);
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
        } else {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No listener specified.");
        }

        JSONObject entryMessage = ConferenceController.createNotifyEntryMessage(clientInterface);
        ConferenceController.placeMessage(entryMessage, clientInterface);

        return responseObject; */
        JSONObject responseObject = new JSONObject();
        responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
        responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "Not implemented");
        return responseObject;
    }
}
