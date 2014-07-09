package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceCommand;
import com.stellarmap.conference.ConferenceController;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class MetadataCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();

        String key = jsonObject.getString("key");
        String value = jsonObject.getString("value");
        if (value!=null && value.trim().equals("")) value = null;

        clientInterface.getConference().setMetadata(key, value);

        responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
        return responseObject;
    }
}
