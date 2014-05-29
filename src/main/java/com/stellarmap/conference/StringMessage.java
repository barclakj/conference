package com.stellarmap.conference;

import org.json.JSONObject;

/**
 * Represents a directly connected client (i.e. from a servlet/web-socket).
 * Created by barclakj on 28/05/2014.
 */
public class StringMessage implements Message {
    private long timestamp = System.currentTimeMillis();
    private String message = null;
    private int originHashCode = 0;

    public StringMessage(String msg) {
        super();
        message = msg;
    }

    @Override
    public void initialise(ConferenceClientInterface listener) {
        this.originHashCode = listener.hashCode();
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

        return jsonObject.toString();
    }

    @Override
    public int getOriginHashCode() {
        return originHashCode;
    }
}
