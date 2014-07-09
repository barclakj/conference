package com.stellarmap.conference;

import org.json.JSONObject;

/**
 * Publisher interface allows subscribes to subscribe to
 * changes in some underlying object. Each subscriber should
 * add itself as an observer to the publisher.
 * When the publisher changes something this then notifies
 * each subscriber of a change. What the subscriber chooses to
 * do is up to them...
 * Created by barclakj on 26/06/2014.
 */
public interface Publisher {
    public void addObserver(Subscriber sub);
    public void publish();
    public JSONObject getJsonContent();
    public String getReferenceId();
    public String getListenerCode();
    public void configure(JSONObject config);
}
