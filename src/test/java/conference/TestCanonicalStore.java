package conference;

import com.stellarmap.cs.CanonicalForm;
import com.stellarmap.cs.CanonicalStore;
import com.stellarmap.cs.ICanonicalStore;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by barclakj on 30/06/2014.
 */
public class TestCanonicalStore extends TestCase {
    private static final String key = "7657896544563456785677980657565";

    private ICanonicalStore canonicalStore = new CanonicalStore();

    @Test
    public void testPut() {
        System.out.println("testPut");
        JSONObject obj = new JSONObject();
        obj.put("ref", key);
        obj.put("ts", System.currentTimeMillis());
        obj.put("updTs", System.currentTimeMillis());
        obj.put("data", "some crazy object value.");

        CanonicalForm cf = CanonicalStore.jsonObjectToCanonicalForm(obj);

        assertNotNull(cf);
        canonicalStore.store(cf);
//        assertTrue(canonicalStore.size()>0);
    }

    @Test
    public void testGet() {
        testPut(); // ensure we have the object in memory.
        System.out.println("testGet");
        CanonicalForm obj = canonicalStore.fetch(key);
        assertTrue(obj!=null);
    }
}
