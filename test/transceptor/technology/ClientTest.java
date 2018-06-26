package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author tristan
 */
public class ClientTest {

    private static String[][] hostlist;
    private static Client client;
    private final String QUERY = "select * from \"GOOGLE-FINANCE-IBM-CLOSE\"";

    @BeforeClass
    public static void setUpClass() throws InterruptedException {
        hostlist = new String[][]{{"localhost", "9000", "-1"}, {"localhost", "9001", "5",}, {"localhost", "9002", "1",}, {"localhost", "9003", "2"}};
        client = new Client("iris", "siri", "test", hostlist, true);

        // Connect
        client.connect(new CompletionHandler() {
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

        }, null);
        Thread.sleep(1000);
        
        // Auth
        client.authenticate(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Authentication OK");
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Authentication failed");
                assertFalse(true);
            }
        }, null);
        Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDownClass() {
        client.close();
    }

    @Test
    public void testRandomConnection() throws InterruptedException {
        Connection connection = client.randomConnection();
        System.out.println("RandomConnection: " + connection.toString());
    }

    @Test
    public void testQuery() throws InterruptedException {
        List<Object> queryResult = new ArrayList<>();
        client.query(QUERY, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                queryResult.add(result);
                System.out.println("Query OK");
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Query failed");
                assertFalse(true);
            }
        }, queryResult);
        Thread.sleep(1000);
        System.out.println("Query result: " + Arrays.toString(queryResult.toArray()));
    }
}
