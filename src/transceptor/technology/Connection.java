package transceptor.technology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transceptor.technology.ProtoMap.*;

/**
 *
 * @author Tristan Nottelman
 */
public class Connection {

    private AsynchronousSocketChannel channel;
    private String username;
    private String password;
    private String dbname;
    private String host;
    private int port;
    private short pid;
    private QPack qpack;

    public Connection(String username, String password, String dbname, String host, int port) {
        this.channel = null;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.host = host;
        this.port = port;
        pid = 0;
        qpack = new QPack();
    }

    /**
     * Converts a number into array of bytes
     *
     * @param number
     * @param size
     * @return
     */
    private byte[] toBytes(long number, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) (number >> (i * 8));
        }
        return result;
    }

    private void channelReader(ByteBuffer readBuffer) {
        readBuffer.clear();

        channel.read(readBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // buffer is ready for read
                //readBuffer.flip();
                System.out.println("result: " + Arrays.toString(readBuffer.array()));
                readBuffer.clear();

                channelReader(readBuffer);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
    }

    private void channelWrite(byte tp, Object data) {
        pid++;
        switch (tp) {
            case CPROTO_REQ_AUTH:
                List<String> list = new ArrayList<>();
                list.add(username);
                list.add(password);
                list.add(dbname);
                byte[] byteMap = qpack.pack(list);
                ByteBuffer writeBuffer = ByteBuffer.allocate(8 + byteMap.length);
                writeBuffer.clear();
                writeBuffer.put(toBytes(byteMap.length, 4)); // length
                writeBuffer.put(toBytes(pid, 2)); // PID
                writeBuffer.put(toBytes(tp, 1)); // tp
                writeBuffer.put(toBytes(tp ^ 255, 1)); // inverted tp
                writeBuffer.put(byteMap);
                writeBuffer.flip();
                
                System.out.println(Arrays.toString(writeBuffer.array()));

                channel.write(writeBuffer, null, new CompletionHandler() {
                    @Override
                    public void completed(Object result, Object attachment) {
                        System.out.println("Success!");
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                });
                break;
        }
    }

    public void connect(CompletionHandler completionHandler) {
        try {
            channel = AsynchronousSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            Future future = channel.connect(address);
            future.get(); //returns null
            System.out.println("Connected: " + channel.isOpen());
            channelReader(channel);
            channelWrite((byte)CPROTO_REQ_AUTH, null);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            channel.shutdownInput();
            channel.close();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
