package com.stellarmap.conference;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Stores the last recieved message for a objects which have a reference key.
 * Created by barclakj on 30/06/2014.
 */
public class CanonicalStore {
    public static Logger log = Logger.getLogger(CanonicalStore.class.getCanonicalName());
    public static final String KEY_REF = "ref";
    public static Map<String, JSONObject> store = new HashMap<String, JSONObject>();

    /**
     * Count the number of objects in the store.
     * @return
     */
    public static long size() {
        return store.size();
    }

    /**
     * Store an object if it has a reference key.
     * @param obj
     */
    public static void store(JSONObject obj) {
        if (obj.has(KEY_REF)) {
            String key = obj.getString(KEY_REF);
            if (key!=null && key.trim()!="") {
                log.info("Storing canonical form for object with key: " + key);
                store.remove(key);
                store.put(key, obj);
            } else {
                log.finest("Reference key is null or blank - ignoring");
            }
        } else {
            log.finest("No reference key found to store.");
        }
    }

    /**
     * Returns the JSON object in the store with the specified key.
     * @param key
     * @return
     */
    public static JSONObject fetch(String key) {
        if (store.containsKey(key)) {
            log.info("Returning canonical form for object found with key: " + key);
            return store.get(key);
        } else {
            log.finest("No canonical form found for object with key: " + key);
            return null;
        }
    }
}
