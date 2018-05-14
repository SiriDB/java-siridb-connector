package transceptor.technology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Short, CompletionHandler> completionHandlers;

    public Connection(String username, String password, String dbname, String host, int port) {
        this.channel = null;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.host = host;
        this.port = port;
        pid = -1;
        qpack = new QPack();
        completionHandlers = new HashMap<>();
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

    private void channelReader() {
        ByteBuffer headerBuffer = ByteBuffer.allocate(Package.HEADER_SIZE);
        headerBuffer.clear();
        channel.read(headerBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                if (result < 0) {
                    // raise exception
                    close();
                    // TODO
                } else if (headerBuffer.remaining() > 0) {
                    channel.read(headerBuffer, null, this);
                } else {
                    // buffer is ready for read
                    Package p = new Package(headerBuffer.array());
                    if (p.getLength() == 0) {
                        // packet without body
                        channelReader();
                    } else {
                        ByteBuffer bodyBuffer = ByteBuffer.allocate(p.getLength());
                        channel.read(bodyBuffer, p, new CompletionHandler<Integer, Object>() {
                            @Override
                            public void completed(Integer result, Object attachment) {
                                System.out.println("result: " + result + " remaining: " + bodyBuffer.remaining());
                                Package p = (Package) attachment;
                                CompletionHandler handler = completionHandlers.remove(p.getPid());
                                if (result < 0) {
                                    // raise exception
                                    close();
                                    handler.failed(new Exception("End of stream"), attachment);
                                } else if (bodyBuffer.remaining() > 0) {
                                    channel.read(bodyBuffer, p, this);
                                } else {
                                    assert (p.getLength() == bodyBuffer.capacity());
                                    p.setBody(bodyBuffer.array());
                                    System.out.println(handler);
                                    handler.completed(1, qpack.unpack(p.getBody()));
                                    channelReader();
                                }
                            }

                            @Override
                            public void failed(Throwable exc, Object attachment) {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                        });
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void channelWrite(byte tp, Object data, CompletionHandler handler) {
        pid++;
        byte[] byteMap = new byte[]{};
        switch (tp) {
            case CPROTO_REQ_AUTH:
                List<String> aList = new ArrayList<>();
                aList.add(username);
                aList.add(password);
                aList.add(dbname);
                byteMap = qpack.pack(aList);
                break;
            case CPROTO_REQ_QUERY:
                List<String> qList = new ArrayList<>();
                qList.add((String) data);
                qList.add(null);
                byteMap = qpack.pack(qList);
                break;
            default:
                throw new IllegalArgumentException("Invalid type " + tp);
        }

        Package pck = new Package(byteMap.length, pid, tp, (byte) (tp ^ 255), byteMap);
        completionHandlers.put(pid, handler);

        channel.write(pck.toByteBuffer(), pck, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.printf("Write %d bytes\n", result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                completionHandlers.remove(((Package) attachment).getPid());
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    public void connect(CompletionHandler completionHandler) {
        try {
            channel = AsynchronousSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            Future future = channel.connect(address);
            future.get(); //returns null
            System.out.println("Connected: " + channel.isOpen());
            channelReader();
            channelWrite((byte) CPROTO_REQ_AUTH, null, null);
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
            channel.close();
        } catch (IOException ex) {
            //Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void query(String query, CompletionHandler handler) {
        channelWrite((byte) CPROTO_REQ_QUERY, query, handler);
    }
}
