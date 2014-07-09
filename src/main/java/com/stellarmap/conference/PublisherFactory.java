package com.stellarmap.conference;

import com.stellarmap.conference.publishers.TimePublisher;
import org.json.JSONObject;

/**
 * Created by barclakj on 26/06/2014.
 */
public class PublisherFactory {

    public synchronized static Publisher obtainPublisher(String name, JSONObject config) {
        Publisher pub = PublisherLog.locatePublisher(name);
        if (pub==null) {
            if (name.startsWith(TimePublisher.PUBLISHER_KEY)) {
                pub = new TimePublisher();
                pub.configure(config);
                PublisherLog.logPublisher(name, pub);
            }
        }
        return pub;
    }
}
