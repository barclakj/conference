package com.stellarmap.conference.rest;

import com.stellarmap.conference.Conference;
import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by barclakj on 07/07/2014.
 */
public class ConferenceRestCmd implements IRestCmd {
    private static Logger log = Logger.getLogger(ConferenceRestCmd.class.getCanonicalName());

    @Override
    public JSONObject doGet(String type, String item) {
        JSONObject response = new JSONObject();
        if (item==null) {
            log.info("Returning all conference summary details.");
            Collection<Conference> allConferences = ConferenceManager.listConferences();
            JSONArray confArray = new JSONArray();
            for(Conference conf : allConferences) {
                JSONObject confItem = conferenceToJSON(conf, false);
                confArray.put(confItem);
            }
            response.put("conferences", confArray);

        } else {
            log.info("Returning conference details for : " + item);
            Conference conf = ConferenceManager.locateConference(item);
            if (conf!=null) {
                JSONObject confItem = conferenceToJSON(conf, true);
                response.put("conference", confItem);
            } else {
                // will return null.
                log.info("Conference '" + item + "' not found.");
                response.put("id", item);
                response.put("msg", "Not found.");
                response.put("code", 404);
            }
        }

       return response;
    }

    /**
     * Returns a JSON representation of a conference.
     * @param conf
     * @param includeMembers
     * @return
     */
    private static JSONObject conferenceToJSON(Conference conf, boolean includeMembers) {
        JSONObject confItem = new JSONObject();
        confItem.put("name", conf.getName());
        confItem.put("ts", conf.getTs());
        confItem.put("id", conf.getConferenceCode());
        confItem.put("maxParticipants", conf.getMaxParticipants());
        confItem.put("participants", conf.getConferenceSize());

        if (includeMembers) {
            Collection<ConferenceClientInterface> allListeners = conf.listListeners();
            JSONArray clientArray = new JSONArray();
            for(ConferenceClientInterface client : allListeners) {
                JSONObject clientItem = new JSONObject();
                clientItem.put("name", client.getName());
                clientItem.put("id", client.getListenerCode());
                clientArray.put(clientItem);
            }
            confItem.put("members", clientArray);
        }

        Map<String, String> metadata = conf.getMetadata();
        JSONArray mdArray = new JSONArray();
        for(String key : metadata.keySet()) {
            JSONObject clientItem = new JSONObject();
            clientItem.put("key", key);
            clientItem.put("value", metadata.get(key) );
            mdArray.put(clientItem);
        }
        confItem.put("metadata", mdArray);
        return confItem;
    }

    @Override
    public JSONObject doPut(String type, String item) {
        return null;
    }

    @Override
    public JSONObject doPost(String type, String item) {
        return null;
    }

    @Override
    public JSONObject doDelete(String type, String item) {
       return null;
    }
}
