package com.stellarmap.conference.publishers;

import com.stellarmap.conference.AbstractPublisher;
import com.stellarmap.conference.PublisherLog;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Created by barclakj on 26/06/2014.
 */
public class TimePublisher extends AbstractPublisher implements Runnable {
    private static Logger log = Logger.getLogger(TimePublisher.class.getCanonicalName());
    public static final String PUBLISHER_KEY = "TIME_PUBLISHER";
    public static int PERIOD = 60000;
    private String locale = null;

    public TimePublisher() {
        super();
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        for(;;) {
            try {
                Thread.sleep(PERIOD); // notify each minute.
            } catch (InterruptedException e) { }
            this.publish(); // publish updates every second
        }
    }

    @Override
    public JSONObject getJsonContent() {
        JSONObject jsonObject = new JSONObject();
        Date d = new Date();
        TimeZone tz = TimeZone.getDefault();
        Locale l = null;
        if (locale==null) {
            l = Locale.getDefault();
        } else {
            l = Locale.forLanguageTag(locale);
        }
        DateFormat formatMask = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, l); // new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss z", l);
        formatMask.setTimeZone(tz);
        String time = formatMask.format(d);
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

    @Override
    public void configure(JSONObject config) {
        locale = null;
        log.info("Configuring TimePublisher.");
        if (config!=null) {
            log.info("Config: " + config.toString());
            if (config.has("locale")) {
                locale = config.getString("locale");
                log.info("Locale set to: " + locale);
            } else {
                log.info("No locale defined - will use system default.");
                locale = Locale.getDefault().toString();
            }
        } else {
            log.info("No configuration provided - will use system default.");
            locale = Locale.getDefault().toString();
        }
    }
}
