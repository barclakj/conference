package com.stellarmap.conference.rest;

import com.stellarmap.cs.CanonicalForm;
import com.stellarmap.cs.CanonicalStore;
import com.stellarmap.cs.ICanonicalStore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by barclakj on 07/07/2014.
 */
public class CanonicalFormAssociationRestCmd implements IRestCmd {

    @Override
    public JSONObject doGet(String type, String item) {
        JSONObject response = null;
        ICanonicalStore cs = new CanonicalStore();
        if (item!=null) {
            Set<String> items = cs.listItems(item);

            response = new JSONObject();
            JSONArray responseArray = new JSONArray();
            for(String it : items) {
                CanonicalForm cf = cs.fetch(it);
                if (it!=null) {
                    JSONObject obj = new JSONObject();
                    obj.put("reference", cf.getReference());
                    obj.put("ts", cf.getTs());
                    obj.put("data", cf.getData());
                    responseArray.put(obj);
                }
            }
            response.put("canonical", responseArray);
            response.put("reference", item);
            response.put("associations", items);
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

        return response;
    }
}
