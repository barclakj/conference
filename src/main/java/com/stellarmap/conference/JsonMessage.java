package com.stellarmap.conference;

import org.json.JSONObject;

/**
 * Represents a directly connected client (i.e. from a servlet/web-socket).
 * Created by barclakj on 28/05/2014.
 */
public class JsonMessage implements Message {
    private long timestamp = System.currentTimeMillis();
    private JSONObject message = null;
    private String originHashCode = null;

    public JsonMessage(JSONObject msg) {
        super();
        message = msg;
    }

    @Override
    public void initialise(ConferenceClientInterface listener) {
        this.originHashCode = listener.getListenerCode();
        timestamp = System.currentTimeMillis();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toJSONString() {
        JSONObject jsonObject = message;

        jsonObject.put("creator", originHashCode);
        jsonObject.put("ts", timestamp);

        return jsonObject.toString();
    }

    @Override
    public String getOriginListenerCode() {
        return originHashCode;
    }
}
