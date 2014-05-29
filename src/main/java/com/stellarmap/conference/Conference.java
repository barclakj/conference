package com.stellarmap.conference;

import com.stellarmap.utils.HashUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A conference represents the local conference room on the current server.
 * Other servers may be running the same conference and will run persistent
 * conference listeners to submit/retrieve messages from a central conference
 * database store.
 *
 * Each conference has multiple conference listeners which represent a client.
 * Clients maintain their own queue of messages.
 * Created by barclakj on 28/05/2014.
 */
public class Conference {
    private static final Logger log = Logger.getLogger(Conference.class.getCanonicalName());

    /**
     * Set of all clients.
     */
    private final Set<ConferenceClientInterface> allListeners = new CopyOnWriteArraySet<ConferenceClientInterface>();

    /**
     * Auditor to record success/failure of delivered messages.
     */
    private final ConferenceAuditor auditor = new ConferenceAuditor();

    /**
     * Executor used for sending messages to client.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Code for the conference.
     */
    private final String conferenceCode =  ConferenceManager.newConferenceCode();

    /**
     * Max participants. If zero then no limit.
     */
    private int maxParticipants = 0;

    /**
     * Sets the max number of participants for the conference.
     * @param num
     */
    public void setMaxParticipants(int num) {
        maxParticipants = num;
    }

    /**
     * Returns the maximum number of participants in the conference.
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Returns the conference code for the current conference.
     * @return
     */
    public String getConferenceCode() {
        return conferenceCode;
    }

    /**
     * Returns the number of members of a conference.
     * @return
     */
    public int members() {
        return allListeners.size();
    }

    /**
     * Adds a new listener to the conference.
     * @param listener
     */
    public void join(ConferenceClientInterface listener) throws ConferenceException {
        // only allow someone to join the conference if not shutdown.
        if (!executor.isShutdown() && (maxParticipants==0 || allListeners.size()<maxParticipants)) {
            listener.setConference(this);
            allListeners.add(listener);
            log.info("Joined conference " + this.getConferenceCode() + ". Now with " + allListeners.size() + " participants.");
        } else {
            log.warning("Failed to join conference " + this.getConferenceCode() + ".");
            if (executor.isShutdown()) throw new ConferenceException("Conference is no longer open");
            else throw new ConferenceException("Conference has reached maximum number of participants");
        }
    }

    /**
     * Close the conference.
     */
    public void close() {
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warning("Exception awaiting termination: " + e.getMessage());
        }
        executor.shutdown();
        allListeners.clear();
    }

    /**
     * Returns a handle on the auditor.
     * @return
     */
    public ConferenceAuditor getAuditor() {
        return auditor;
    }

    /**
     * Removes the specified listener from the conference.
     * @param listener
     */
    public void remove(ConferenceClientInterface listener) {
        allListeners.remove(listener);
    }

    /**
     * Posts the specified message to all listeners bar the originating listener.
     * @param msg
     */
    public void put(Message msg) {
        if (log.isLoggable(Level.INFO)) log.info("Attempting to deliver message: " + msg.hashCode());
        if (msg!=null) {
            // place message on the queue for each listener
            // (note that this does not actually deliver the message).
            log.info("Queuing...");
            for(ConferenceClientInterface listener : allListeners) {
                if (listener.hashCode()!=msg.getOriginHashCode()) {
                    if (log.isLoggable(Level.INFO)) log.info("Queuing message for: " + listener.hashCode());
                    listener.tickle(msg);
                } else {
                    if (log.isLoggable(Level.INFO)) log.info("Skipping message for: " + listener.getListenerCode() + " as origin.");
                }
            }

            // now try and deliver them using the executor.
            // note that multiple threads may be spawned.
            log.info("Delivering...");
            if (executor!=null) {
                for(ConferenceClientInterface listener : allListeners) {
                    if (log.isLoggable(Level.INFO)) log.info("Delivering messages to: " + listener.getListenerCode());
                    ClientInterfaceRunner runner = listener.getClientInterfaceRunner();
                    if (runner!=null) {
                        executor.submit(listener.getClientInterfaceRunner());
                    } else {
                        if (log.isLoggable(Level.WARNING)) log.warning("No runner available for listener: " + listener.hashCode());
                    }
                }
            }
        }
    }
}
