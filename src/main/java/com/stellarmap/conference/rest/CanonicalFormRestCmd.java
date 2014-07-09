package com.stellarmap.conference.rest;

import com.stellarmap.cs.CanonicalForm;
import com.stellarmap.cs.CanonicalStore;
import com.stellarmap.cs.ICanonicalStore;
import org.json.JSONObject;

/**
 * Created by barclakj on 07/07/2014.
 */
public class CanonicalFormRestCmd implements IRestCmd {

    @Override
    public JSONObject doGet(String type, String item) {
        JSONObject response = null;
        ICanonicalStore cs = new CanonicalStore();
        if (item!=null) {
            CanonicalForm cf = cs.fetch(item);
            if (cf!=null) {
                response = new JSONObject();
                response.put("reference", cf.getReference());
                response.put("ts", cf.getTs());
                response.put("data", cf.getData());
            }
        } else {
            // do nothing - we're not listing all items!
        }
        return response;
    }

    @Override
    public JSONObject doPut(String type, String item) {
        return null;
    }

    @Override
    public JSONObject doPost(String type, String item) {
        return null;
    }

    @Override
    public JSONObject doDelete(String type, String item) {
        JSONObject response = null;
        ICanonicalStore cs = new CanonicalStore();
        if (item!=null) {
            CanonicalForm cf = cs.fetch(item);
            if (cs.delete(cf)) {
                response = new JSONObject();
                response.put("result", "ok");
            } else {
                response = new JSONObject();
                response.put("result", "failed");
            }
        } else {
            // do nothing - we're not deleting all items!
        }
        return response;
    }
}
