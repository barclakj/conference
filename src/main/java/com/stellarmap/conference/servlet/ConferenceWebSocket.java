package com.stellarmap.conference.servlet;

import com.stellarmap.conference.*;

import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
@ServerEndpoint("/conf")
public class ConferenceWebSocket {
    private static final Logger log = Logger.getLogger(ConferenceWebSocket.class.getCanonicalName());
    private MessageCourier courier = null;
    private ConferenceClientInterface clientInterface = null;

    class MessageCourier implements ClientCourier {
        public RemoteEndpoint.Async endpoint = null;

        @Override
        public boolean deliver(Message msg) {
            if (endpoint!=null) {
                log.info("Sending message: " + msg.toJSONString());
                endpoint.sendText(msg.toJSONString());
       //         try {
        //            courier.endpoint.flushBatch();
         //       } catch (IOException e) {
          //          log.warning("Unable to send message - exception on flush! " + e.getMessage());
           //         e.printStackTrace();
            //    }
                return true;
            } else {
                log.warning("Unable to send message - no endpoint!");
                return false;
            }
        }
    }

    @OnMessage
    public String handleMessage(String msg, Session session) {
        System.out.println("handleMessage");

        if (courier==null) {
            log.info("Creating courier and client interface: ");
            courier = new MessageCourier();
            clientInterface = new DirectConferenceClientInterface();
            ((DirectConferenceClientInterface)clientInterface).setCourier(courier);
        }
        courier.endpoint = session.getAsyncRemote();
        if (session.isOpen()) {

            log.info("Session is open.");
        } else {
            log.info("Session is NOT OPEN.");
        }

        if (msg!=null && msg.startsWith("join ")) {
            log.info("Joining conference:: " + msg);
            String confCode = msg.substring(5);
            Conference conf = ConferenceManager.locateConference(confCode);
            if (conf==null) {
                conf = ConferenceManager.newConference();
            }
            try {
                conf.join(clientInterface);
            } catch (ConferenceException e) {
                log.warning("Failed to join conference: " + e.getMessage());
            }
            return conf.getConferenceCode();
        } else {
            log.info("Sending message: " + msg);
            StringMessage conferenceMessage = new StringMessage(msg);
            log.info("Created message...");
            if (clientInterface==null) {
                log.info("Client is null!!!");
            } else {
                log.info("Placing message!!!");
                clientInterface.put(conferenceMessage);
                log.info("Sent");
            }
            return null;
        }
    }

}
