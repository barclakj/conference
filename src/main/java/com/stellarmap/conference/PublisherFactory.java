package com.stellarmap.conference;

import com.stellarmap.conference.publishers.TimePublisher;

/**
 * Created by barclakj on 26/06/2014.
 */
public class PublisherFactory {

    public synchronized static Publisher obtainPublisher(String name) {
        Publisher pub = PublisherLog.locatePublisher(name);
        if (pub==null) {
            if (name.equalsIgnoreCase(TimePublisher.PUBLISHER_KEY)) {
                pub = new TimePublisher();
                PublisherLog.logPublisher(TimePublisher.PUBLISHER_KEY, pub);
            }
        }
        return pub;
    }
}
