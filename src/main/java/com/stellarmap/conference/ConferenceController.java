package com.stellarmap.conference;

import com.stellarmap.conference.cmds.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ConferenceController {
    private static final Logger log = Logger.getLogger(ConferenceController.class.getCanonicalName());
    public static final String CONF_COMMAND = "cmd";
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

    public static final String NAME = "name";

    public static final String CREATE_CONFERENCE = "createConference";
    public static final String LIST_LISTENERS = "listListeners";
    public static final String LIST_CONFERENCES = "listConferences";
    public static final String MOVE_LISTENER = "moveListener";
    public static final String CLOSE_CONFERENCE = "closeConference";
    public static final String JOIN_CONFERENCE = "joinConference";
    public static final String SET_CI_NAME = "setClientInterface";
    public static final String BROADCAST_MESSAGE = "broadcastMsg";
    public static final String SUBSCRIBE_MESSAGE = "subscribe";
    public static final String READ_REF_MESSAGE = "readRef";

    /**
     * Map of commands to command strings.
     */
    private static Map<String, ConferenceCommand> commandMap = new HashMap<String, ConferenceCommand>();

    /**
     * Register default command options.
     */
    static {
        try {
            registerCommand(new CreateConferenceCmd(), CREATE_CONFERENCE);
            registerCommand(new ListConferencesCmd(), LIST_CONFERENCES);
            registerCommand(new ListListenersCmd(), LIST_LISTENERS);
            registerCommand(new MoveListenerCmd(), MOVE_LISTENER);
            registerCommand(new CloseConferenceCmd(), CLOSE_CONFERENCE);
            registerCommand(new JoinConferenceCmd(), JOIN_CONFERENCE);
            registerCommand(new SetConferenceClientInterfaceNameCmd(), SET_CI_NAME);
            registerCommand(new BroadcastMessageCmd(), BROADCAST_MESSAGE);
            registerCommand(new SubscribeCmd(), SUBSCRIBE_MESSAGE);
            registerCommand(new ReadCanonicalFormCmd(), READ_REF_MESSAGE);
        } catch(ConferenceException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

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
     * Registers a specific command against a string command.
     * Note that conference command objects are not thread safe like web servlets.
     * @param cmd
     * @param command
     */
    public static void registerCommand(ConferenceCommand cmd, String command) throws ConferenceException {
        if (commandMap.containsKey(command)) {
            throw new ConferenceException("Command '" + command + "' already exists.");
        } else {
            commandMap.put(command, cmd);
        }
    }

    /**
     * Retrieves command by name (or null and log if not found).
     * @param command
     * @return
     */
    public static ConferenceCommand getCommand(String command) {
        if (commandMap.containsKey(command)) return commandMap.get(command);
        else {
            log.log(Level.WARNING, "Request to obtain command '" + command + "' failed - does not exist!");
            return null;
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
            command = getCommand(jsonObject.getString(CONF_COMMAND));
            if (command!=null) {
                // execute the object if it is not null.
                responseJsonObject = command.cmd(jsonObject, clientInterface);
            } else {
                log.warning("Unknown command type (" + jsonObject.getString(CONF_COMMAND) + ") specified.");
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
            JSONObject msg = jsonObject.getJSONObject(CONF_MSG);
            m = new JsonMessage(msg); //StringMessage(msg);
            m.initialise(clientInterface);
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
