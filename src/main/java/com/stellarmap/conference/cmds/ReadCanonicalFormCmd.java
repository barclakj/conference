package com.stellarmap.conference.cmds;

import com.stellarmap.conference.CanonicalStore;
import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.ConferenceCommand;
import com.stellarmap.conference.ConferenceController;
import org.json.JSONObject;

/**
 * Command to read the canonical form from the canonical store.
 * Created by barclakj on 29/05/2014.
 */
public class ReadCanonicalFormCmd implements ConferenceCommand {
    public static final String READ_KEY = "reference";

    @Override
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        if (jsonObject.has(READ_KEY)) {
            JSONObject obj = CanonicalStore.fetch(jsonObject.getString(READ_KEY));
            return obj;
        } else {
            return null;
        }
    }
}
