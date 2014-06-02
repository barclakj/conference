package com.stellarmap.conference;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class Conference implements Runnable {
    private static final Logger log = Logger.getLogger(Conference.class.getCanonicalName());

    /**
     * Timestamp of when the conference was created.
     */
    private final long ts = System.currentTimeMillis();
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

    private AtomicBoolean stateRunning = new AtomicBoolean(true);
    private ExecutorService conferenceRunner = Executors.newSingleThreadExecutor();

    /**
     * Indicates if the conference should expire once all participants have left.
     */
    private boolean expireWhenEmpty = true;

    public Conference() {
        super();
    }

    /**
     * Set expiry on conf.
     * @param _val
     */
    public void setExpireWhenEmpty(boolean _val) {
        expireWhenEmpty = _val;
    }

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
        log.info("Closing conference: " + this.getConferenceCode());
        stateRunning.set(false);
        try {
            conferenceRunner.awaitTermination(500, TimeUnit.MILLISECONDS);
            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warning("Exception awaiting termination: " + e.getMessage());
        }
        conferenceRunner.shutdown();
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
     * If no-one is left then shutdown the conference.
     * @param listener
     */
    public void remove(ConferenceClientInterface listener) {
        if (log.isLoggable(Level.FINEST)) log.finest("Removing listener..");
        allListeners.remove(listener);
        if (allListeners.size()==0 && expireWhenEmpty) {
            if (log.isLoggable(Level.FINEST)) log.finest("It's zero size - killing it.");
            this.close();
            if (log.isLoggable(Level.FINEST)) log.finest("Dropping conference.");
            ConferenceManager.dropConference(this);
        }
    }

    /**
     * Removes the specified listener from the conference by name.
     * If no-one is left then shutdown the conference.
     * @param clientListener
     */
    public void remove(String clientListener) {
        if (log.isLoggable(Level.FINEST)) log.finest("Removing listener: " + clientListener);
        for(ConferenceClientInterface cl : allListeners) {
            if (log.isLoggable(Level.FINEST)) log.finest("Comparing " + clientListener + " with " + cl.getListenerCode());
            if (cl.getListenerCode().equals(clientListener)) {
                if (log.isLoggable(Level.FINEST)) log.finest("Listener found! ");
                remove(cl);
            }
        }
    }

    /**
     * Posts the specified message to all listeners bar the originating listener.
     * @param msg
     */
    public void put(Message msg) {
        if (log.isLoggable(Level.FINEST)) log.finest("Attempting to deliver message: " + msg.hashCode());
        if (msg!=null) {
            // place message on the queue for each listener
            // (note that this does not actually deliver the message).
            log.finest("Queuing...");
            for(ConferenceClientInterface listener : allListeners) {
                if (listener.getListenerCode()!=msg.getOriginListenerCode()) {
                    if (log.isLoggable(Level.FINEST)) log.finest("Queuing message for: " + listener.getListenerCode());
                    listener.tickle(msg);
                } else {
                    if (log.isLoggable(Level.FINEST)) log.finest("Skipping message for: " + listener.getListenerCode() + " as origin.");
                }
            }
            // now deliver messages...
            if (!conferenceRunner.isTerminated()) {
                conferenceRunner.execute(this);
            }
        }
    }

    /**
     * Executes each interface runner for the conference.
     * Note that this then checks that the number of messages queued is zero
     * and may run again (and again) if it does not.
     */
    public void run() {
        if (stateRunning.get()) {
            boolean keepRunning = true;
            while (keepRunning) {
                // now try and deliver them using the executor.
                // note that multiple threads may be spawned.
                log.finest("Delivering...");
                if (executor!=null) {
                    for(ConferenceClientInterface listener : allListeners) {
                        if (log.isLoggable(Level.FINEST)) log.finest("Delivering messages to: " + listener.getListenerCode());
                        ClientInterfaceRunner runner = listener.getClientInterfaceRunner();
                        if (runner!=null) {
                            executor.submit(listener.getClientInterfaceRunner());
                        } else {
                            if (log.isLoggable(Level.WARNING)) log.warning("No runner available for listener: " + listener.hashCode());
                        }
                    }
                }
                keepRunning = false; // assume we can stop.. (should be able to)

                // but check again anyway.. if we've any messages left over...
                for(ConferenceClientInterface listener : allListeners) {
                    if (!listener.getMessageQueue().isEmpty()) {
                        // run again.
                        keepRunning = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns the collection of listeners.
     * @return
     */
    public Collection<ConferenceClientInterface> listListeners() {
        return allListeners;
    }

    /**
     * Returns the number of members in the conference.
     * @return
     */
    public int getConferenceSize() {
        return allListeners.size();
    }

    /**
     * Returns the timestamp of when the conference was created.
     * @return
     */
    public long getTs() {
        return ts;
    }
}
