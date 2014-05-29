package com.stellarmap.conference;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ClientInterfaceRunner implements Runnable {
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
                // if we have a valid client
                if (client!=null) {
                    // get the queue
                    Queue<Message> messageQueue = client.getMessageQueue();
                    // and the conference details.
                    Conference conf = client.getConference();
                    // and auditor
                    ConferenceAuditor auditor = conf.getAuditor();
                    // and courier
                    ClientCourier courier = client.getCourier();
                    // check valid and has messages
                    while (courier!=null && conf!=null && messageQueue!=null && messageQueue.size()>0) {
                        // for each message, poll the queue..
                        Message msg = messageQueue.poll();
                        // and if msg is ok, deliver...
                        if (msg!=null) {
                            // note that deliver may fail in which case the message
                            // will never have been delivered. we cannot do much about
                            // this here but call auditor to record the outcome.
                            if (courier.deliver(msg)) {
                                if (auditor!=null) auditor.notifySuccess(client, msg);
                            } else {
                                if (auditor!=null) auditor.notifyFailure(client, msg);
                            }
                        }

                        // check for an interrupt.
                        if (Thread.interrupted()) {
                            // thread has been interrupted... cleanup and return;
                            messageQueue = null;
                            conf = null;
                            auditor = null;
                            courier = null;
                        }
                    }
                }
            } finally {
                // finally unlock the lock...
                lock.unlock();
            }
        }
    }
}
