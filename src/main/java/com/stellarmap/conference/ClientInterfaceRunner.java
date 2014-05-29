package com.stellarmap.conference;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ClientInterfaceRunner implements Runnable {
    private static final Logger log = Logger.getLogger(ClientInterfaceRunner.class.getCanonicalName());
    /**
     * Local var for client interface
     */
    private ConferenceClientInterface client = null;

    /**
     * Lock to prevent multiple concurrent runners sending messages to the client.
     */
    private final Lock lock = new ReentrantLock();

    public ClientInterfaceRunner(ConferenceClientInterface _client) {
        super();
        this.client = _client;
    }

    /**
     * For the specified client, loop around pending messages
     * and send accordingly.
     */
    public void run() {
        // try the lock and proceed if available else backout.
        if (lock.tryLock()) {
            try {
                log.finest("Lock successful. Attempting to deliver messages.");
                // if we have a valid client
                if (client!=null) {

                    // get the queue
                    Queue<Message> messageQueue = client.getMessageQueue();
                    int targetCount = messageQueue.size();
                    if (targetCount>0) {
                        // we have messages to send
                        // and the conference details.
                        Conference conf = client.getConference();
                        // and auditor
                        ConferenceAuditor auditor = conf.getAuditor();
                        // and courier
                        ClientCourier courier = client.getCourier();

                        int messageCount = 0;

                        // check valid and has messages
                        for(int i=0;i<targetCount;i++) {
//                        while (courier!=null && conf!=null && messageQueue!=null && messageQueue.size()>0) {
                            log.finest("All valid... polling for next message.");
                            // for each message, poll the queue..
                            Message msg = messageQueue.poll();
                            messageCount++;
                            // and if msg is ok, deliver...
                            if (msg!=null) {
                                log.finest("Message found. Proceeding to send");
                                // note that deliver may fail in which case the message
                                // will never have been delivered. we cannot do much about
                                // this here but call auditor to record the outcome.
                                if (courier.deliver(msg)) {
                                    if (log.isLoggable(Level.FINEST)) log.finest("Delivering messages to: " + client.getListenerCode());
                                    if (auditor!=null) auditor.notifySuccess(client, msg);
                                } else {
                                    if (log.isLoggable(Level.INFO)) log.info("Delivering messages to: " + client.getListenerCode());
                                    if (auditor!=null) auditor.notifyFailure(client, msg);
                                }
                            } else {
                                break;
                            }

                            // check for an interrupt.
                            if (Thread.interrupted()) {
                                log.info("Interrupted during send... Halting..");
                                // thread has been interrupted... cleanup and return;
                                messageQueue = null;
                                conf = null;
                                auditor = null;
                                courier = null;
                            }
                        }
                        log.info("Sent " + messageCount + " messages from total " + targetCount);
                    }
                }
            } finally {
                // finally unlock the lock...
                lock.unlock();
                log.finest("Unlock successful.");
            }
        } else {
            log.finest("Lock already in use. Aborting.");
        }
    }
}
