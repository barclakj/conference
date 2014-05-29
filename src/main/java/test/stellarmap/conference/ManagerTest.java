package test.stellarmap.conference;

import com.stellarmap.conference.*;
import junit.framework.TestCase;
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
        Conference conf = ConferenceManager.newConference(2);
        ConferenceClientInterface heldci = null;

        // join 10 participants
        for(int i=0;i<3;i++) {
            DirectConferenceClientInterface ci = new DirectConferenceClientInterface();
            ci.setCourier(new DummyCourier());
            try {
                conf.join(ci);
                heldci = ci;
            } catch (ConferenceException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i<10;i++) {
            // send 200 messages to each one;
            Message msg = new StringMessage("Hello");
            heldci.put(msg);
        }

        conf.close();
    }
}
