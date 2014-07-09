package com.stellarmap.conference;

import com.stellarmap.utils.HashUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
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

    static {
        // by default there should always be one conference called "theredlion"
        Conference conf = new Conference();
        conf.setMaxParticipants(0);
        conf.setExpireWhenEmpty(false);
        conf.setName("The Red Lion");

        allConferenceMap.put("the_red_lion", conf); // temp hack: note that the name will not match the conference code
    }

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
     * Closes and removes the specified conference.
     * @param confCode
     */
    public static void closeConference(String confCode) {
        if(confCode!=null) {
            Conference conf = locateConference(confCode);
            if (conf!=null) {
                allConferenceMap.remove(confCode);
                conf.close();
                conf = null;
            }
        }
    }

    /**
     * Removes conference listener by code from all conferences.
     * @param listenerCode
     */

    /**
     * Moves listener from one conference to another (if it exists).
     * @param clientInterface
     * @param confCode
     * @throws ConferenceException
     */
    public static void moveListener(ConferenceClientInterface clientInterface, String confCode) throws ConferenceException {
        closeListener(clientInterface);

        //now find the new conference and join it if found
        if (confCode!=null) {
            Conference conf = locateConference(confCode);
            if (conf!=null) {
                conf.join(clientInterface);
            } // if here then listener will die once socket is closed.
        } // if here then listener will die once socket is closed.
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
            Conference selectedConference = null;
            // above should have worked unless we've been asked by name or for the red lion.
            // scan through all conferences to locate - slower but less likely to be used.
            Collection<Conference> conferences = allConferenceMap.values();
            for(Conference conf : conferences) {
                if (conf!=null && (conf.getConferenceCode().equals(confCode) || conf.getName().equals(confCode))) {
                    selectedConference = conf;
                    break;
                }
                conf = null;
            }

            if (selectedConference==null && log.isLoggable(Level.WARNING))
                log.warning("Request for conference: " + confCode + " failed - not found!");
            else log.info("Returning (scan) handle on conference: " + confCode);
            return selectedConference;
        }
    }

    /**
     * Returns the collection of conferences.
     * @return
     */
    public static Collection<Conference> listConferences() {
        return allConferenceMap.values();
    }

    /**
     * Drops a conference out of the map. Called only from conference
     * directly.
     * @param conf
     */
    protected static void dropConference(Conference conf) {
        allConferenceMap.remove(conf.getConferenceCode());
    }

    /**
     * Removes the listener from all conferences and drains the queue.
     * @param clientInterface
     */
    public static void closeListener(ConferenceClientInterface clientInterface) {
        // remove the listener from any conferences
        for(Conference conf : allConferenceMap.values()) {
            conf.remove(clientInterface.getListenerCode());
        }
        // nullify conference
        clientInterface.setConference(null);
        // drain any messages out.
        clientInterface.drainQueue();
    }
}
