package com.stellarmap.conference;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ConferenceAuditor {
    private static final Logger log = Logger.getLogger(ConferenceAuditor.class.getCanonicalName());

    /**
     * Records that a message was successfully sent.
     * @param client
     * @param msg
     */
    public void notifySuccess(ConferenceClientInterface client, Message msg) {
        if (log.isLoggable(Level.INFO)) log.info("Successful delivery to client: " + client.getListenerCode() + " for message: " + msg.hashCode());
    }

    /**
     * Records that a message was not successfully sent.
     * @param client
     * @param msg
     */
    public void notifyFailure(ConferenceClientInterface client, Message msg) {
        if (log.isLoggable(Level.WARNING)) log.warning("Failed delivery to client: " + client.getListenerCode() + " for message: " + msg.hashCode());
    }
}
