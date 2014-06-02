package com.stellarmap.conference.cmds;

import com.stellarmap.conference.Conference;
import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ListConferencesCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();
        JSONArray confArray = new JSONArray();
        for(Conference conf : ConferenceManager.listConferences()) {
            JSONObject confRecord = new JSONObject();
            confRecord.put(ConferenceController.CONFERENCE_CODE, conf.getConferenceCode());
            confRecord.put(ConferenceController.NAME, conf.getName());
            confRecord.put(ConferenceController.CONFERENCE_SIZE, conf.getConferenceSize());
            confRecord.put(ConferenceController.MAX_PARTICIPANTS, conf.getMaxParticipants());
            confRecord.put(ConferenceController.TIMESTAMP, conf.getTs());
            confArray.put(confRecord);
        }
        responseObject.put(ConferenceController.CONF_LIST_OF_CONFERENCES, confArray);

        return responseObject;
    }
}
