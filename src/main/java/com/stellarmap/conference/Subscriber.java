package com.stellarmap.conference;

/**
 * Allows a conference to subcsribe to changes in some underlying object.
 * Created by barclakj on 26/06/2014.
 */
public interface Subscriber {
    /**
     * Handles notification from a publisher that something has changed.
     * The publisher can be used to obtain a json representation of the
     * changed object and reference id.
     * @param pub
     */
    public void publish(Publisher pub);

    /**
     * Called when subscribing to a publisher.
     * @param pub
     */
    public void subscribe(Publisher pub);
}
