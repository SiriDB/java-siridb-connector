package transceptor.technology;

import java.nio.channels.CompletionHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tristan
 */
public class ClientTest {

    private Client client;
    private String[][] hostlist = new String[][] {{"localhost", "9000", "-1"}, {"localhost", "9001", "5"}, {"localhost", "9002", "1"}, {"localhost", "9003", "2"}};

    public ClientTest() {
        client = new Client("iris", "siri", "test", hostlist, true);
    }

    @Test
    public void testPriority() throws InterruptedException {

        CompletionHandler handler = new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Connection OK");
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Connection failed");
                assertFalse(true);
            }

        };
        client.connect(handler);

        Thread.sleep(5000);
        Connection c = client.randomConnection();
        System.out.println(c.toString());
        client.close();
    }
}
