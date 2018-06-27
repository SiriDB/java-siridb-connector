package example;

import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import transceptor.technology.Connection;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

        Connection conn = new Connection("iris", "siri", "test", "localhost", 9000, false);

        // Connect
        CountDownLatch countDownLatch = new CountDownLatch(1);
        conn.connect(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                countDownLatch.countDown();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Connection failed: " + exc.getMessage());
            }
        }, null);

        countDownLatch.await();

        // Authenticate
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        conn.authenticate(new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                countDownLatch2.countDown();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Connection failed: " + exc.getMessage());
            }
        }, null);

        countDownLatch2.await();

        Random random = new Random();
        Map<String, Number[][]> map = new HashMap<>();
        map.put("some_measurements", new Number[][]{{System.currentTimeMillis(), ((random.nextInt(21) - 10) / 10.0)}});
        conn.insert(map, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                ((LinkedBlockingQueue) attachment).add(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("exception: " + exc.getMessage());
            }
        }, queue);

        Object result = queue.take();
        System.out.println("Insert result: " + result);

        conn.query("select * from \"some_measurements\"", new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                ((LinkedBlockingQueue) attachment).add(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("exception: " + exc.getMessage());
            }
        }, queue);

        result = queue.take();
        System.out.println("Query result: " + result);
        conn.close();
    }
}
