package transceptor.technology;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Tristan Nottelman
 */
public class PackageTest {

    private static QPack qpack;
    private final String QUERY = "select * from \"GOOGLE-FINANCE-IBM-CLOSE\"";
    
    @BeforeClass
    public static void setUpClass() {
        qpack = new QPack();
    }
    
    @Test
    public void testPackageLength() {
        Package pck = new Package(new byte[]{54, -27, 0, 0, 2, 0, 0, -1});
        assertEquals(pck.getLength(), 58678);
    }
    
    @Test
    public void testCheckbit() {
        Package pck = new Package(0, (short)0, (byte)3, null);
        assertTrue(pck.isValid());
    }
    
    @Test
    public void testPackageArray() {
        
        byte[] body = qpack.pack(QUERY);
        
        Package pck = new Package(body.length, (short)0, (byte)3, body);
        
        System.out.println("arr: " + Arrays.toString(pck.toByteBuffer().array()));
    }
}
