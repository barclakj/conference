package com.stellarmap.conference;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by barclakj on 28/05/2014.
 */
public class DirectConferenceClientInterface implements ConferenceClientInterface {
    private Conference conference = null;
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
    private final ClientInterfaceRunner ciRunner = new ClientInterfaceRunner(this);
    private String listenerCode = null;

    private ClientCourier courier = null;

    private String name = null;

    public DirectConferenceClientInterface() {
        super();
        listenerCode = ConferenceManager.newListenerCode(null, null, null);
    }

    public DirectConferenceClientInterface(String key1, String key2, String key3) {
        super();
        listenerCode = ConferenceManager.newListenerCode(key1, key2, key3);
    }


    @Override
    public void tickle(Message msg) {
        messageQueue.add(msg);
    }

    @Override
    public void setConference(Conference conf) {
        conference = conf;
    }

    @Override
    public Conference getConference() {
        return conference;
    }

    @Override
    public void put(Message msg) {
        msg.initialise(this);
        if (conference!=null) conference.put(msg);
    }

    @Override
    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    @Override
    public ClientCourier getCourier() {
        return courier;
    }

    @Override
    public ClientInterfaceRunner getClientInterfaceRunner() {
        return ciRunner;
    }

    /**
     * Sets the courier to that specified.
     * @param courier
     */
    public void setCourier(ClientCourier courier) {
        this.courier = courier;
    }

    @Override
    public String getListenerCode() {
        return listenerCode;
    }

    @Override
    public void drainQueue() {
        messageQueue.clear();
    }

    @Override
    public void setName(String _name) {
        name = _name;
    }

    @Override
    public String getName() {
        if (name==null || name.trim().equalsIgnoreCase("")) {
            return this.getListenerCode();
        } else {
            return name;
        }
    }
}
