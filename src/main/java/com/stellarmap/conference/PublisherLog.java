package com.stellarmap.conference;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a log of all publishers.
 * Created by barclakj on 26/06/2014.
 */
public class PublisherLog {
    private static Map<String, Publisher> allPublishers = new HashMap<String, Publisher>();

    /**
     * Returns a publisher if it already exists.
     * @param key
     * @return
     */
    public static Publisher locatePublisher(String key) {
        if (allPublishers.containsKey(key)) {
            return allPublishers.get(key);
        } else {
            return null;
        }
    }

    /**
     * Adds a publisher to the log.
     * @param key
     * @param pub
     */
    public static void logPublisher(String key, Publisher pub) {
        allPublishers.put(key, pub);
    }
}
