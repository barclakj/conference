package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceCommand;
import com.stellarmap.conference.ConferenceController;
import org.json.JSONObject;

/**
 * Created by barclakj on 29/05/2014.
 */
public class BroadcastMessageCmd implements ConferenceCommand {

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        ConferenceController.placeMessage(jsonObject, clientInterface);
        return null;
    }
}
