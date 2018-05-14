package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tristan Nottelman
 */
public class ConnectionTest {

    private Connection connection;
    
    public ConnectionTest() {
        connection = new Connection("iris", "siri", "test", "localhost", 9000);
    }
    
    @Test
    public void testConnect() throws InterruptedException {
        connection.connect(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Test");
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue(false);
            }
        
        });
        connection.query("select * from \"GOOGLE-FINANCE-IBM-CLOSE\"", new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Test: query written");
                System.out.println("Result: " + attachment);
                
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Total failure");
            }
        });
        Thread.sleep(5000);
        connection.close();
    }
}
