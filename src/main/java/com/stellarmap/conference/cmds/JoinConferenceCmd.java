package com.stellarmap.conference.cmds;

import com.stellarmap.conference.*;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class JoinConferenceCmd implements  ConferenceCommand {
    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
       JSONObject responseObject = new JSONObject();
       String confCode = jsonObject.getString(ConferenceController.CONFERENCE_CODE);
       try {
            if (confCode!=null) {
                ConferenceManager.moveListener(clientInterface, confCode);
                JSONObject entryMessage = ConferenceController.createNotifyEntryMessage(clientInterface);
                ConferenceController.placeMessage(entryMessage, clientInterface);
                responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
                responseObject.put(ConferenceController.CONF_OUTCOME_MSG, confCode);
                confCode = null;
            } else {
                responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
                responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "Unable to join the conference (conference not found).");
            }
        } catch (ConferenceException e) {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "Unable to join the conference (none specified).");
        }

        return responseObject;
    }
}
