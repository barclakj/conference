package com.stellarmap.conference;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default auditor. Needs generalisation.
 * Created by barclakj on 29/05/2014.
 */
public class ConferenceAuditor {
    private static final Logger log = Logger.getLogger(ConferenceAuditor.class.getCanonicalName());
    public static long messageTransferCount = 0;
    public static long messageFailureCount = 0;
    public static long messageSuccessCount = 0;

    /**
     * Records that a message was successfully sent.
     * @param client
     * @param msg
     */
    public void notifySuccess(ConferenceClientInterface client, Message msg) {
        messageTransferCount++;
        messageSuccessCount++;
        if (log.isLoggable(Level.INFO)) log.info("Successful delivery to client: " + client.getListenerCode() + " for message: " + msg.hashCode());
    }

    /**
     * Records that a message was not successfully sent.
     * @param client
     * @param msg
     */
    public void notifyFailure(ConferenceClientInterface client, Message msg) {
        messageTransferCount++;
        messageFailureCount++;
        if (log.isLoggable(Level.WARNING)) log.warning("Failed delivery to client: " + client.getListenerCode() + " for message: " + msg.hashCode());
    }

    public static void resetMetrics() {
        messageFailureCount=0;
        messageSuccessCount=0;
        messageTransferCount=0;
    }
}
