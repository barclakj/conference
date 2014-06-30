package conference;

import com.stellarmap.conference.CanonicalStore;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by barclakj on 30/06/2014.
 */
public class TestCanonicalStore extends TestCase {
    private static final String key = "7657896544563456785677980657565";
    @Test
    public void testPut() {
        System.out.println("testPut");
        JSONObject obj = new JSONObject();
        obj.put("ref", key);
        obj.put("action", "dosomething");
        obj.put("object", "some crazy object value.");

        CanonicalStore.store(obj);
        assertTrue(CanonicalStore.size()>0);
    }

    @Test
    public void testGet() {
        testPut(); // ensure we have the object in memory.
        System.out.println("testGet");
        JSONObject obj = CanonicalStore.fetch(key);
        assertTrue(obj!=null);
    }
}
