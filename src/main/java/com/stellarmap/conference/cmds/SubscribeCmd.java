package com.stellarmap.conference.cmds;

import com.stellarmap.conference.*;
import org.json.JSONObject;

/**
 * Creates a new conference and joins it.
 * Created by barclakj on 29/05/2014.
 */
public class SubscribeCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseObject = new JSONObject();

        if (jsonObject.has("subscriptionKey")) {
            String key = jsonObject.getString("subscriptionKey");
            Publisher pub = PublisherFactory.obtainPublisher(key);
            if (pub!=null) {
                Conference conf = clientInterface.getConference();
                conf.subscribe(pub);

                responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_SUCCESS);
            } else {
                responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
                responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No publisher located: " + key);
            }
        } else {
            responseObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
            responseObject.put(ConferenceController.CONF_OUTCOME_MSG, "No subscription key specified.");
        }

        return responseObject;
    }
}
