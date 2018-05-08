package transceptor.technology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author Tristan Nottelman
 */
public class Test {

    /**
     * Converts a number into array of bytes
     *
     * @param number
     * @param size
     * @return
     */
    public static byte[] toBytes(long number, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) (number >> (i * 8));
        }
        return result;
    }

    /**
     *
     * @param target
     * @param toAdd
     * @param pos
     * @return
     */
    public static byte[] addToArray(byte[] target, byte[] toAdd, int pos) {
        System.arraycopy(toAdd, 0, target, pos, toAdd.length);
        return target;
    }

    /*public static void read(ByteBuffer b, AsynchronousSocketChannel channel) {
        b = ByteBuffer.allocate(8);
        b.clear();

        channel.read(b, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // buffer is ready for read
                //readBuffer.flip();
                System.out.println("result: " + Arrays.toString(b.array()));
                b.clear();

                channel.read(b, null, new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        // buffer is ready for read
                        //readBuffer.flip();
                        System.out.println("result: " + Arrays.toString(b.array()));
                        b.clear();
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
    }*/

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        long time = System.currentTimeMillis();
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress("localhost", 9000);
        Future future = channel.connect(address);
        future.get(); //return null

        time = System.currentTimeMillis() - time;
        System.out.printf("Connected, took %d milisec\n", time);

        // create header
        ByteBuffer writeBuffer = ByteBuffer.allocate(8);
        writeBuffer.clear();
        writeBuffer.put(toBytes(0, 4)); // length
        writeBuffer.put(toBytes(0, 2)); // PID
        writeBuffer.put(toBytes(3, 1)); // tp
        writeBuffer.put(toBytes(3 ^ 255, 1)); // inverted tp
        writeBuffer.flip();

        channel.write(writeBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // writing has been completed
                System.out.println("write0 completed: " + result);

                // wait for respond
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.clear();
        buffer.put(toBytes(0, 4)); // length
        buffer.put(toBytes(1, 2)); // PID
        buffer.put(toBytes(0, 1)); // tp
        buffer.put(toBytes(0 ^ 255, 1)); // inverted tp
        buffer.flip();

        channel.write(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // writing has been completed
                System.out.println("write1 completed: " + result);

                // wait for respond
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        Thread.sleep(1000);

        // create header
        writeBuffer.clear();
        writeBuffer.put(toBytes(0, 4)); // length
        writeBuffer.put(toBytes(0, 2)); // PID
        writeBuffer.put(toBytes(3, 1)); // tp
        writeBuffer.put(toBytes(3 ^ 255, 1)); // inverted tp
        writeBuffer.flip();
        channel.write(writeBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // writing has been completed
                System.out.println("write2 completed: " + result);

                // wait for respond
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
        Thread.sleep(3000);
//        try (SocketChannel socketChannel = SocketChannel.open()) {
//            socketChannel.configureBlocking(false);
//            socketChannel.connect(new InetSocketAddress("localhost", 9000));
//
//            // create header
//            ByteBuffer buf = ByteBuffer.allocate(8);
//            buf.clear();
//            buf.put(toBytes(0, 4)); // length
//            buf.put(toBytes(0, 2)); // PID
//            buf.put(toBytes(3, 1)); // tp
//            buf.put(toBytes(3 ^ 255, 1)); // inverted tp
//            buf.flip();
//
//            // write
//            while (buf.hasRemaining()) {
//                int bytesWritten = socketChannel.write(buf);
//                System.out.println("wrote " + bytesWritten + " bytes");
//            }
//            
//            /*siridb.query("select * from 'bla'", onQueryResult);
//            
//            f.onFinised(onQueryResult)
//                    
//            _my_pids = {}
//            f = my_pids.get(pid)
//            f.done(data, 0);*/
//            
//            // read
//            int bytesRead = socketChannel.read(buf);
//            buf.flip();
//            byte[] b = buf.array();
//            System.out.println(Arrays.toString(b));
//            buf.clear();
//        }
    }
}
