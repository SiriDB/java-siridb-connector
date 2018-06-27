package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Tristan Nottelman
 */
public class ConnectionTest {

    private static Connection connection;
    private final String QUERY_WRONG = "select * fom \"GOOGLE-FINANCE-IBM-CLOSE\"";
    private final String QUERY = "select * from \"GOOGLE-FINANCE-IBM-CLOSE\"";
    private final String INSERT_SERIES = "GOOGLE-FINANCE-IBM-CLOSE";

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        connection = new Connection("iris", "siri", "test", "localhost", 9000, true);

        // Connect
        connection.connect(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                assertTrue("Connection OK", true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to connect: " + exc.getMessage(), false);
            }

        }, null);

        Thread.sleep(1000);

        // Auth
        connection.authenticate(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                assertTrue("Authentication OK", true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Authentication failed: " + exc.getMessage());
                assertTrue("Authentication failed", false);
            }
        }, null);
    }

    @AfterClass
    public static void tearDownClass() {
        connection.close();
    }

    @Test
    public void testQuery() throws InterruptedException {
        connection.query(QUERY, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Test: query written");
                System.out.println("Result: " + result);
                System.out.println("Attachment: " + attachment);
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to execute query: " + exc.getMessage(), false);
            }
        }, null);
        Thread.sleep(1000);
    }

    @Test
    public void testWrongQuery() throws InterruptedException {
        connection.query(QUERY_WRONG, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                assertTrue(false);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Query Wrong result: " + exc.getMessage());
                assertEquals(exc.getClass(), QueryErrorException.class);
            }
        }, null);
        Thread.sleep(5000);
    }

    @Test
    public void testInsert() throws InterruptedException {
        Map<String, Number[][]> map = new HashMap();
        Random random = new Random();

        map.put(INSERT_SERIES, new Number[][]{{System.currentTimeMillis(), ((random.nextInt(21) - 10) / 10.0)}});
        connection.insert(map, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Test: insert written");
                System.out.println("Result: " + result);
                System.out.println("Attachment: " + attachment);
                assertTrue(true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to execute insert: " + exc.getMessage(), false);
            }

        }, null);
        Thread.sleep(5000);
    }
}
