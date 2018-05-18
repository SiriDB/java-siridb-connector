package transceptor.technology;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tristan Nottelman
 */
public class PackageTest {

    private QPack qpack = new QPack();
    
    @Test
    public void testPackageLength() {
        Package pck = new Package(new byte[]{54, -27, 0, 0, 2, 0, 0, -1});
        assertEquals(pck.getLength(), 58678);
    }
    
    @Test
    public void testCheckbit() {
        Package pck = new Package(0, (short)0, (byte)3, null);
        assertEquals(pck.getId() ^ 255, pck.getCheckbit());
    }
    
    @Test
    public void testPackageArray() {
        
        byte[] body = qpack.pack("select * from \"GOOGLE-FINANCE-IBM-CLOSE\"");
        
        Package pck = new Package(body.length, (short)0, (byte)3, body);
        
        
        System.out.println("arr: " + Arrays.toString(pck.toByteBuffer().array()));
        
    }
}
