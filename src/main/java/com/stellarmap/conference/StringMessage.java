package com.stellarmap.conference;

import org.json.JSONObject;

/**
 * Represents a directly connected client (i.e. from a servlet/web-socket).
 * Created by barclakj on 28/05/2014.
 */
public class StringMessage implements Message {
    private long timestamp = System.currentTimeMillis();
    private String message = null;
    private String originHashCode = null;
    private String name = null;

    public StringMessage(String msg) {
        super();
        message = msg;
    }

    @Override
    public void initialise(ConferenceClientInterface listener) {
        this.originHashCode = listener.getListenerCode();
        this.name = listener.getName();
        timestamp = System.currentTimeMillis();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("msg", message);
        jsonObject.put("creator", originHashCode);
        jsonObject.put("ts", timestamp);
        jsonObject.put("name", name);

        return jsonObject.toString();
    }

    @Override
    public String getOriginListenerCode() {
        return originHashCode;
    }
}
