package com.stellarmap.conference;

import java.util.Queue;

/**
 * Each conference listener represents a client listening for messages in the conference.
 * Created by barclakj on 28/05/2014.
 */
public interface ConferenceClientInterface {
    /**
     * Notifies the listener that a new message is waiting
     * and attempts should be made to pick up these new messages.
     * Note that this does not send, just queues it to be sent.
     */
    public void tickle(Message msg);

    /**
     * Sets the conference for the listener.
     * @param conf
     */
    public void setConference(Conference conf);

    /**
     * Returns the conference handle.
     * @return
     */
    public Conference getConference();

    /**
     * Submits a new message to the conference.
     * @param msg
     */
    public void put(Message msg);

    /**
     * Returns a handle on the message queue.
     * @return
     */
    public Queue<Message> getMessageQueue();

    /**
     * Gets the courier who will deliver the message.
     * @return
     */
    public ClientCourier getCourier();

    /**
     * Returns a handle on the runner who will deal with delivering messages.
     * @return
     */
    public ClientInterfaceRunner getClientInterfaceRunner();

    /**
     * Returns the listener code.
     * @return
     */
    public String getListenerCode();

    /**
     * Removes (does not process) all messages in the queue.
     */
    public void drainQueue();

    /**
     * Set the logical name of the client interface. Note that if none (or null/zero length string (trimmed))
     * is specified then the listener code will be used as the name.
     * @param _name
     */
    public void setName(String _name);

    /**
     * Returns the logical name of the client interface.
     * @return
     */
    public String getName();
}
