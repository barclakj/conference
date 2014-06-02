package com.stellarmap.conference.cmds;

import com.stellarmap.conference.Conference;
import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ListListenersCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();

        Conference conf = null;
        String confCode=null;
        if (jsonObject.has(ConferenceController.CONFERENCE_CODE)) {
            confCode = jsonObject.getString(ConferenceController.CONFERENCE_CODE);
            conf = ConferenceManager.locateConference(confCode);
        } else {
            conf = clientInterface.getConference();
        }

        if (conf!=null) {
            JSONObject confRecord = new JSONObject();
            confRecord.put(ConferenceController.CONFERENCE_CODE, conf.getConferenceCode());
            confRecord.put(ConferenceController.NAME, conf.getName());
            confRecord.put(ConferenceController.CONFERENCE_SIZE, conf.getConferenceSize());
            confRecord.put(ConferenceController.MAX_PARTICIPANTS, conf.getMaxParticipants());
            confRecord.put(ConferenceController.TIMESTAMP, conf.getTs());
            responseObject.put(ConferenceController.CONFERENCE, confRecord);

            JSONArray confArray = new JSONArray();
            for(ConferenceClientInterface ci : conf.listListeners()) {
                JSONObject listRecord = new JSONObject();
                listRecord.put(ConferenceController.CONF_LISTENER_CODE, ci.getListenerCode());
                listRecord.put(ConferenceController.NAME, ci.getName());
                confArray.put(listRecord);
            }
            responseObject.put(ConferenceController.CONF_LIST_OF_LISTENERS, confArray);
        } else {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No conference specified or conference not found.");
        }

        return responseObject;
    }
}
