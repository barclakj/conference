package com.stellarmap.conference.cmds;

import com.stellarmap.conference.ConferenceClientInterface;
import com.stellarmap.conference.JsonMessage;
import com.stellarmap.conference.Message;
import com.stellarmap.conference.StringMessage;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ConferenceController {
    private static final Logger log = Logger.getLogger(ConferenceController.class.getCanonicalName());
    public static final String CONF_COMMAND = "confCmd";
    public static final String CONF_OUTCOME = "cmdOutcome";
    public static final String CONF_ACTION = "confAction";
    public static final String CONF_ACTION_STATE_CHANGE = "statechange";

    public static final String CONF_STATUS = "status";

    public static final String CONF_STATUS_ENTRY = "entry";
    public static final String CONF_STATUS_ACTIVE = "active";
    public static final String CONF_STATUS_EXIT = "exit";

    public static final String CONFERENCE_CODE = "confCode";
    public static final String CONF_LISTENER_CODE = "listenerCode";

    public static final String CONFERENCE_SIZE = "size";
    public static final String MAX_PARTICIPANTS = "maxParticipants";
    public static final String TIMESTAMP = "timestamp";

    public static final String CONF_OUTCOME_SUCCESS = "success";
    public static final String CONF_OUTCOME_FAIL = "failure";
    public static final String CONF_OUTCOME_MSG = "outcomeMsg";

    public static final String CONF_LIST_OF_CONFERENCES = "listOfConferences";
    public static final String CONF_LIST_OF_LISTENERS = "listOfListeners";
    public static final String CONFERENCE = "conference";

    public static final String CONF_MSG = "msg";

    public static final int CREATE_CONFERENCE = 0;
    public static final int LIST_LISTENERS = 1;
    public static final int LIST_CONFERENCES = 2;
    public static final int MOVE_LISTENER = 3;
    public static final int CLOSE_CONFERENCE = 4;
    public static final int JOIN_CONFERENCE = 5;

    // converts string to cmds object
    public static JSONObject toJsonObject(String jsonString) {
        JSONObject object = new JSONObject(jsonString);
        return object;
    }

    // checks if the object is a command.
    public static boolean isConferenceCommand(JSONObject jsonObject) {
       if (jsonObject.has(CONF_COMMAND)) {
            // it's a command!
            return true;
        } else {
            return false;
        }
    }

    /**
     * Processes a command as defined in a JSON object for the specified client interface.
     * @param jsonObject
     * @param clientInterface
     * @return
     */
    public static JSONObject processCommand(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        JSONObject responseJsonObject = null;
        if (isConferenceCommand(jsonObject)) {
            ConferenceCommand command = null;
            int cmd = jsonObject.getInt(CONF_COMMAND);
            switch (cmd) {
                case CREATE_CONFERENCE:
                    command = new CreateConferenceCmd();
                    break;
                case LIST_LISTENERS:
                    command = new ListListenersCmd();
                    break;
                case LIST_CONFERENCES:
                    command = new ListConferencesCmd();
                    break;
                case MOVE_LISTENER:
                    command = new MoveListenerCmd();
                    break;
                case CLOSE_CONFERENCE:
                    command = new CloseConferenceCmd();
                    break;
                case JOIN_CONFERENCE:
                    command = new JoinConferenceCmd();
                    break;
                default:
                    break;
            }
            // execute the object if it is not null.
            if (command!=null) {
                responseJsonObject = command.cmd(jsonObject, clientInterface);
            } else {
                responseJsonObject = new JSONObject();
                responseJsonObject.put(ConferenceController.CONF_OUTCOME, ConferenceController.CONF_OUTCOME_FAIL);
                responseJsonObject.put(ConferenceController.CONF_OUTCOME_MSG, "Undefined command requested.");
            }
        }
        return responseJsonObject;
    }

    /**
     * Submits a message.
     * @param jsonObject
     * @param clientInterface
     */
    public static void placeMessage(JSONObject jsonObject, ConferenceClientInterface clientInterface) {
        Message m = null;
        if (jsonObject!=null && jsonObject.has(CONF_MSG)) {
            String msg = jsonObject.getString(CONF_MSG);
            m = new StringMessage(msg);
        } else if (jsonObject!=null) {
            m = new JsonMessage(jsonObject);
        }
        if (m!=null) clientInterface.put(m);
        else {
            log.warning("Unable to send message: " + jsonObject);
        }
    }


    /**
     * Creates a new object to notify that a user has left the conference.
     * @param clientInterface
     * @return
     */
    public static JSONObject createNotifyExitMessage(ConferenceClientInterface clientInterface) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ConferenceController.CONF_ACTION, ConferenceController.CONF_ACTION_STATE_CHANGE);
        jsonObject.put(ConferenceController.CONF_LISTENER_CODE, clientInterface.getListenerCode());
        jsonObject.put(ConferenceController.CONF_STATUS, ConferenceController.CONF_STATUS_EXIT);
        return jsonObject;
    }

    /**
     * Creates a new object to notify that a user has entered the conference.
     * @param clientInterface
     * @return
     */
    public static JSONObject createNotifyEntryMessage(ConferenceClientInterface clientInterface) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ConferenceController.CONF_ACTION, ConferenceController.CONF_ACTION_STATE_CHANGE);
        jsonObject.put(ConferenceController.CONF_LISTENER_CODE, clientInterface.getListenerCode());
        jsonObject.put(ConferenceController.CONF_STATUS, ConferenceController.CONF_STATUS_ENTRY);
        return jsonObject;
    }
}
