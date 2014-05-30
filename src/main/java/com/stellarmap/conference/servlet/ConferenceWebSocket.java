package com.stellarmap.conference.servlet;

import com.stellarmap.conference.*;
import com.stellarmap.conference.cmds.ConferenceController;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
@ServerEndpoint("/conf")
public class ConferenceWebSocket {
    private static final Logger log = Logger.getLogger(ConferenceWebSocket.class.getCanonicalName());
    private MessageCourier courier = null;
    private ConferenceClientInterface clientInterface = null;

    private static final long TIMEOUT = 10000; // 10s timeout

    /**
     * Inner class to handle delivery of messages (i.e. a clientcourier).
     * If message send fails then assume closed and kill this/client interface.
     */
    class MessageCourier implements ClientCourier, SendHandler {
        public RemoteEndpoint.Async endpoint = null;

        @Override
        public boolean deliver(Message msg) {
            if (endpoint!=null) {
                if (log.isLoggable(Level.FINEST)) log.finest("Sending message: " + msg.toJSONString());
                endpoint.sendText(msg.toJSONString(), this);
                return true;
            } else {
                log.warning("Unable to send message - no endpoint!");
                return false;
            }
        }

        @Override
        public void onResult(SendResult sendResult) {
            if (sendResult.isOK()) {
                log.finest("Send successful.");
            } else {
                Throwable t = sendResult.getException();
                if (t!=null) {
                    log.warning("Failed to send response (removing client). Exception: " + t.getMessage());
                } else {
                    log.warning("Failed to send response (removing client). No exception.");
                }
                if (courier!=null) courier.endpoint = null;
                if (clientInterface!=null) clientInterface.getConference().remove(clientInterface);
            }
        }
    }

    @OnMessage
    public String onMessage(String msg, Session session) {
        boolean abort = false;

        // verifying session
        if (session.isOpen()) {
            if (log.isLoggable(Level.FINEST)) log.finest("Session is open.");
        } else {
            log.warning("Session is not open! Aborting.");
            cleanup();
            abort = true;
        }

        // first check if the instruction is to join a conference and do so accordingly.
        if (!abort) {

            // Check if message is a new conference to join
            if (msg!=null) {
                JSONObject jsonObject = ConferenceController.toJsonObject(msg);
                if (ConferenceController.isConferenceCommand(jsonObject)) {
                    if (log.isLoggable(Level.INFO)) log.info("Msg:" + jsonObject.toString());
                    JSONObject response = ConferenceController.processCommand(jsonObject, clientInterface);
                    String responseMsg = response.toString();
                    return responseMsg;
                } else {
                    ConferenceController.placeMessage(jsonObject, clientInterface);
                    return null;
                }
            } else {
                log.warning("Unable to process request - no message.");
                return "{\"error\": \"Unable to process request.\"}";
            }
        } else {
            log.warning("Unable to process request - no session.");
            return "{\"error\": \"Unable to process request.\"}";
        }
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.fine("Session opened.");
        log.info("Creating courier and client interface: ");
        courier = new MessageCourier();
        courier.endpoint = session.getAsyncRemote();
        courier.endpoint.setSendTimeout(TIMEOUT);
        clientInterface = new DirectConferenceClientInterface(session.getId(), session.getProtocolVersion(), session.getNegotiatedSubprotocol());
        ((DirectConferenceClientInterface)clientInterface).setCourier(courier);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException  {
        if (log.isLoggable(Level.FINE)) log.fine("Socket closed reason: [Code: " + reason.getCloseCode() + "] " + reason.getReasonPhrase());
        cleanup();
    }

    @OnError
    public void onError(Throwable t) {
        log.log(Level.SEVERE, "Exception in WebSocket: " + t.getMessage(), t);
        cleanup();
    }

    /**
     * Cleans up instance objects and removes listener from all conferences.
     * A message will be sent to all other participants of the meeting that
     * someone has joined.
     */
    private void cleanup() {
        log.fine("Cleaning up web-socket");
        if (clientInterface!=null) {
            if (clientInterface.getConference()!=null) {
                JSONObject jsonObject = ConferenceController.createNotifyExitMessage(clientInterface);
                ConferenceController.placeMessage(jsonObject, clientInterface);
            }
            ConferenceManager.closeListener(clientInterface);
            clientInterface =  null;
        }
        if (courier!=null) courier.endpoint = null;
    }



}
