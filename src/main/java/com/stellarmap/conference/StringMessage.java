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
        if (listener!=null) {
            this.originHashCode = listener.getListenerCode();
            this.name = listener.getName();
        }
        timestamp = System.currentTimeMillis();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(ConferenceController.CONF_COMMAND, ConferenceController.BROADCAST_MESSAGE);
        jsonObject.put(ConferenceController.CONF_MSG, message);
        jsonObject.put("creator", originHashCode);
        jsonObject.put("ts", timestamp);
        if (this.getReference()!=null) jsonObject.put("ref", this.getReference());

        return jsonObject.toString();
    }

    @Override
    public String getOriginListenerCode() {
        return originHashCode;
    }

    @Override
    public String getReference() {
        return name;
    }
}
