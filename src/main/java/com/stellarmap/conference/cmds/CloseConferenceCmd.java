package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceCommand;
import com.stellarmap.conference.ConferenceController;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONObject;

/**
 * Closes a conference.
 * Created by barclakj on 29/05/2014.
 */
public class CloseConferenceCmd implements ConferenceCommand {


    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();
        String confCode = jsonObject.getString(ConferenceController.CONFERENCE_CODE);
        if(confCode!=null) {
            ConferenceManager.closeConference(confCode);
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
        } else {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No conference code specified.");
        }
        return responseObject;
    }
}
