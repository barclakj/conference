package com.stellarmap.conference;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by barclakj on 26/06/2014.
 */
public abstract class AbstractPublisher implements Publisher {
    private Set<Subscriber> pubSubs = new HashSet<Subscriber>();

    @Override
    public void addObserver(Subscriber sub) {
        if (sub!=null) pubSubs.add(sub);
    }

    @Override
    public void publish() {
        for(Subscriber sub : pubSubs) {
            sub.publish(this);
        }
    }
}
