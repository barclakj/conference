package conference;

import com.stellarmap.conference.*;
import com.stellarmap.conference.publishers.TimePublisher;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by barclakj on 29/05/2014.
 */
public class ManagerTest extends TestCase {
    @Test
    public void testCode() {
        for(int i=0;i<10;i++) {
            System.out.println(ConferenceManager.newConferenceCode());
        }
    }

    @Test
    public void testConference() {
        // create conference
        ConferenceAuditor.resetMetrics();
        int MAX_PARTICIPANTS = 3;
        Conference conf = ConferenceManager.newConference(MAX_PARTICIPANTS);
        ConferenceClientInterface heldci = null;

        // join 10 participants
        for(int i=0;i<(MAX_PARTICIPANTS+1);i++) {
            DirectConferenceClientInterface ci = new DirectConferenceClientInterface();
            ci.setCourier(new DummyCourier());
            try {
                conf.join(ci);
                ci.setName("CI " + i);
                heldci = ci;
                if (i>MAX_PARTICIPANTS) {
                    fail("Max participants exceeded!");
                }
            } catch (ConferenceException e) {
                e.printStackTrace();
                assertTrue(i==MAX_PARTICIPANTS);
            }

        }
        int msgs = 10;
        for(int i=0;i<msgs;i++) {
            // send messages to each one;
            Message msg = new StringMessage("Hello");
            heldci.put(msg);
        }
        int totalMsgs = (MAX_PARTICIPANTS-1) * msgs;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Target: " + totalMsgs + " Sent: " + ConferenceAuditor.messageTransferCount + " Success: " + ConferenceAuditor.messageSuccessCount + " Failures: " + ConferenceAuditor.messageFailureCount );
        assertTrue(totalMsgs == ConferenceAuditor.messageSuccessCount);

        conf.close();
    }

    @Test
    public void testPublisher() {
        TimePublisher.PERIOD = 100;
        ConferenceAuditor.resetMetrics();
        System.out.println("Testing publisher...");
        Conference conf = ConferenceManager.newConference();
        DirectConferenceClientInterface ci = new DirectConferenceClientInterface();
        ci.setCourier(new DummyCourier());
        try {
            conf.join(ci);
        } catch (ConferenceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        JSONObject obj = new JSONObject();
        obj.put("locale", "en_GB");
        Publisher pub = PublisherFactory.obtainPublisher(TimePublisher.PUBLISHER_KEY, obj);
        conf.subscribe(pub);

        System.out.println("Waiting 5 seconds for x5 messages to be published...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Target: >1 Sent: " + ConferenceAuditor.messageTransferCount + " Success: " + ConferenceAuditor.messageSuccessCount + " Failures: " + ConferenceAuditor.messageFailureCount );

        assertTrue(ConferenceAuditor.messageSuccessCount>0);

        conf.close();
    }
}
