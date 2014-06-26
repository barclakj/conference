package com.stellarmap.conference.publishers;

import com.stellarmap.conference.AbstractPublisher;
import com.stellarmap.conference.PublisherLog;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by barclakj on 26/06/2014.
 */
public class TimePublisher extends AbstractPublisher implements Runnable {
    public static final String PUBLISHER_KEY = "TIME_PUBLISHER";

    public TimePublisher() {
        super();
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        for(;;) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
            this.publish(); // publish updates every second
        }
    }

    @Override
    public JSONObject getJsonContent() {
        JSONObject jsonObject = new JSONObject();
        String time = (new Date()).toString();
        jsonObject.put("time", time);
        return jsonObject;
    }

    @Override
    public String getReferenceId() {
        return PUBLISHER_KEY;
    }

    @Override
    public String getListenerCode() {
        return null;
    }
}
