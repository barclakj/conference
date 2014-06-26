package com.stellarmap.conference;

import com.stellarmap.conference.ConferenceClientInterface;
import org.json.JSONObject;

/**
 * Accepts a cmds object and executes a command against the conference environment
 * and returns result as a new cmds object.
 * Created by barclakj on 29/05/2014.
 */
public interface ConferenceCommand {
    public JSONObject cmd(JSONObject jsonObject, ConferenceClientInterface clientInterface);
}
