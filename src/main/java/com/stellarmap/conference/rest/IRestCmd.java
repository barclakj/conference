package com.stellarmap.conference.rest;

import org.json.JSONObject;

/**
 * Created by barclakj on 07/07/2014.
 */
public interface IRestCmd {
    public JSONObject doGet(String type, String item);
    public JSONObject doPut(String type, String item);
    public JSONObject doPost(String type, String item);
    public JSONObject doDelete(String type, String item);
}
