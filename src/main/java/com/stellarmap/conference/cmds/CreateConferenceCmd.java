package com.stellarmap.conference.cmds;

import com.stellarmap.conference.*;
import org.json.JSONObject;

/**
 * Creates a new conference and joins it.
 * Created by barclakj on 29/05/2014.
 */
public class CreateConferenceCmd  implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();

        Conference conf = ConferenceManager.newConference();
        try {
            ConferenceManager.moveListener(clientInterface, conf.getConferenceCode());
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, conf.getConferenceCode());
            conf = null;
        } catch (ConferenceException e) {
            ConferenceManager.closeConference(conf.getConferenceCode());
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "Unable to join the conference.");
        }

        return responseObject;
    }
}
