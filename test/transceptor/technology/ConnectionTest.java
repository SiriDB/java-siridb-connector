package transceptor.technology;

import java.nio.channels.CompletionHandler;
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
        Thread.sleep(5000);
        connection.close();
    }
}
