package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
        connection.connect(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.println("Test");
                assertTrue("Connected", true);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to connect: " + exc.getMessage(), false);
            }

        });
    }

    @Test
    public void testQuery() throws InterruptedException {
        connection.query("select * from \"GOOGLE-FINANCE-IBM-CLOSE\"", new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                assertTrue(true);
                System.out.println("Test: query written");
                System.out.println("Result: " + attachment);

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to execute query: " + exc.getMessage(), false);
            }
        });
        Thread.sleep(5000);
        connection.close();
    }

    @Test
    public void testInsert() throws InterruptedException {
        Map<String, Number[][]> map = new HashMap();
        Random random = new Random();

        map.put("GOOGLE-FINANCE-IBM-CLOSE", new Number[][]{{System.currentTimeMillis(), ((random.nextInt(21) - 10) / 10.0)}});
        connection.insert(map, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                assertTrue(true);
                System.out.println("Test: insert written");
                System.out.println("Result: " + attachment);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                assertTrue("Failed to execute insert: " + exc.getMessage(), false);
            }

        });
        Thread.sleep(5000);
        connection.close();
    }
}
