package com.stellarmap.conference;

/**
 * Created by barclakj on 29/05/2014.
 */
public interface ClientCourier {
    /**
     * Delivers the specified message to the recipient.
     * Returns true if successful else false.
     * @param msg
     * @return True if successful deliver, else false.
     */
    public boolean deliver(Message msg);
}
