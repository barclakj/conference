package test.stellarmap.conference;

import com.stellarmap.conference.ClientCourier;
import com.stellarmap.conference.Message;

/**
 * Created by barclakj on 29/05/2014.
 */
public class DummyCourier implements ClientCourier {
    @Override
    public boolean deliver(Message msg) {
        long delay = (long)((Math.random()*1000));
        try {
            System.out.println("Waiting for " + delay + "ms");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Delivered: " + msg.hashCode());
        return true;
    }
}
