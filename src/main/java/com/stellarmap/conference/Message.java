package com.stellarmap.conference;

/**
 * Created by barclakj on 28/05/2014.
 */
public interface Message {
    /**
     * Initialise the message (sets timestamp and sets the conference listener);
     */
    public void initialise(ConferenceClientInterface listener);

    /**
     * Returns the timestamp when the message was created.
     * @return
     */
    public long getTimestamp();

    /**
     * Returns a JSON string representation of the message;
     * @return
     */
    public String toJSONString();

    /**
     * Returns the hash code of the originating listener.
     * @return
     */
    public String getOriginListenerCode();

    /**
     * Returns a logical reference for the message. Could be the name of the sender
     * or some other meaningful reference.
     * @return
     */
    public String getReference();
}
