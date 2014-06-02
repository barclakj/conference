package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONObject;

/**
 * Sets the name of the client interface to that specified.
 * Created by barclakj on 29/05/2014.
 */
public class SetConferenceClientInterfaceNameCmd implements ConferenceCommand {


    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();
        String name = jsonObject.getString(ConferenceController.NAME);
        if(name!=null) {
            clientInterface.setName(name);
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
        } else {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No name specified.");
        }
        return responseObject;
    }
}
