package com.stellarmap.conference;

import com.stellarmap.utils.HashUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ConferenceManager {
    private static final Logger log = Logger.getLogger(ConferenceManager.class.getCanonicalName());

    /**
     * Singleton atomic counter of conferences in the server.
      */
    private static int CONFERENCE_COUNTER = 0;

    /**
     * Singleton atomic counter of listeners in the server.
     */
    private static int LISTENER_COUNTER = 0;

    /**
     * Map of all conferences.
     */
    private static final Map<String, Conference> allConferenceMap = new ConcurrentHashMap<String, Conference>();

    /**
     * Utility method to create conference with unlimited clients.
     * @return
     */
    public static Conference newConference() {
        return newConference(0);
    }
    /**
     * Construct a new conference and add it to the conference map for future use.
     * Returns the new conference.
     * @return
     */
    public static Conference newConference(int size) {
        Conference conf = new Conference();
        conf.setMaxParticipants(size);
        allConferenceMap.put(conf.getConferenceCode(), conf);
        return conf;
    }

    /**
     * Constructs a unique conference code.
     * Conf code is a base 64, hashed version of the local machine address, time, random number
     * and a singleton conference counter. This should be globally unique unless multiple
     * JVM's on a single machine are running (and even here it is highly unlikely a clash
     * will occur though not impossible).
     * @return
     */
    public static String newConferenceCode() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warning("Unable to determine host address. Using 'UNKNOWN_HOST'.");
            host = "UNKNOWN_HOST";
        }
        CONFERENCE_COUNTER++; // increment counter for request..
        String openCode = host + ":" + System.currentTimeMillis() + ":" + CONFERENCE_COUNTER + ":" + (int)(Math.random()*1000000);

        String hashedCode = HashUtils.hash(openCode);
        if (log.isLoggable(Level.INFO)) log.info("Hashed conference code: " + hashedCode + " for " + openCode);
        openCode = null;

        return hashedCode;
    }

    /**
     * Constructs a unique listener code.
     * Listener code is a base 64, hashed version of the local machine address, time, random number
     * and a singleton conference counter combined with 3 optional keys. These keys
     * should be; for example, client user-agent, client IP address and client session ID.
     * This should be globally unique unless multiple
     * JVM's on a single machine are running (and even here it is highly unlikely a clash
     * will occur though not impossible).
     * @return
     */
    public static String newListenerCode(String key1, String key2, String key3) {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warning("Unable to determine host address. Using 'UNKNOWN_HOST'.");
            host = "UNKNOWN_HOST";
        }
        LISTENER_COUNTER++; // increment counter for request..
        String openCode = key1 + key2 + key3 + host + ":" + System.currentTimeMillis() + ":" + CONFERENCE_COUNTER + ":" + (int)(Math.random()*1000000);

        String hashedCode = HashUtils.hash(openCode);
        if (log.isLoggable(Level.INFO)) log.info("Hashed listener code: " + hashedCode + " for " + openCode);
        openCode = null;

        return hashedCode;
    }

    /**
     * Locates a conference by conference code and returns if found.
     * @param confCode
     * @return
     */
    public static Conference locateConference(String confCode) {
        if (allConferenceMap.keySet().contains(confCode)) {
            if (log.isLoggable(Level.INFO)) log.info("Returning handle on conference: " + confCode);
            return allConferenceMap.get(confCode);
        } else {
            if (log.isLoggable(Level.WARNING)) log.warning("Request for conference: " + confCode + " failed - not found!");
            return null;
        }
    }
}
