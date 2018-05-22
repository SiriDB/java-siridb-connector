package transceptor.technology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
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
    private short packageId;
    private QPack qpack;
    private Map<Short, CompletionHandler> completionHandlers;

    /**
     * Create a new instance of Connection with credentials, database name, host
     * and port
     *
     * @param username
     * @param password
     * @param dbname
     * @param host
     * @param port
     */
    public Connection(String username, String password, String dbname, String host, int port) {
        this.channel = null;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.host = host;
        this.port = port;
        packageId = -1;
        qpack = new QPack();
        completionHandlers = new HashMap<>();
    }

    /**
     * This method handles the data input
     */
    private void channelReader() {
        ByteBuffer headerBuffer = ByteBuffer.allocate(Package.HEADER_SIZE);
        headerBuffer.clear();
        channel.read(headerBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                if (result < 0) {
                    close();
                } else if (headerBuffer.remaining() > 0) {
                    channel.read(headerBuffer, null, this);
                } else {
                    Package p = new Package(headerBuffer.array());
                    if (p.getLength() == 0) {
                        // packet without body
                        channelReader();
                    } else {
                        bodyReader(p);
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

    /**
     * 
     * @param p 
     */
    private void bodyReader(Package p) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(p.getLength());
        channel.read(bodyBuffer, p, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("result: " + result + " remaining: " + bodyBuffer.remaining());
                Package p = (Package) attachment;
                CompletionHandler handler = completionHandlers.remove(p.getId());

                // checkbit
                if (!p.isValid()) {
                    handler.failed(new InvalidPackageException("Invalid package, received type "
                            + ((p.getType() ^ 255)) + " but checkbit was "
                            + (p.getCheckbit())), attachment);
                }
                if (result < 0) {
                    // end of stream
                    close();
                    handler.failed(new Exception("End of stream"), attachment);
                } else if (bodyBuffer.remaining() > 0) {
                    channel.read(bodyBuffer, p, this);
                } else {
                    if (p.getLength() != bodyBuffer.capacity()) {
                        handler.failed(new InvalidPackageException("Invalid package, received "
                                + p.getLength() + "bytes but expected was "
                                + bodyBuffer.capacity()), attachment);
                    }
                    p.setBody(bodyBuffer.array());
                    try {
                        Object o = qpack.unpack(p.getBody());
                        handler.completed(1, o);
                    } catch (Exception e) {
                        handler.failed(e, attachment);
                    }
                    channelReader();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    /**
     * This method handles the data written to SiriDB
     *
     * @param packageType
     * @param data
     * @param handler
     */
    private void channelWriter(byte packageType, byte[] data, CompletionHandler handler) {
        packageId++;
        packageId = (short) (packageId % Short.MAX_VALUE);
        Package pck = new Package(data.length, packageId, packageType, data);

        completionHandlers.put(packageId, handler);

        channel.write(pck.toByteBuffer(), pck, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.printf("ChannelWrite: write %d bytes\n", result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                completionHandlers.remove(((Package) attachment).getId());
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    /**
     * Connect to SiriDB (authentication included)
     *
     * @param handler
     */
    public void connect(CompletionHandler handler) {
        try {
            // connect to SiriDB
            channel = AsynchronousSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            Future future = channel.connect(address);
            if (future.get() != null) {
                handler.failed(new Exception("Failed to connect"), null);
            } //returns null if successful

            // start channel reader
            channelReader();

            // authentication
            channelWriter((byte) CPROTO_REQ_AUTH, qpack.pack(new String[]{username, password, dbname}), handler);
        } catch (IOException | InterruptedException | ExecutionException ex) {
            handler.failed(ex, null);
        }
    }

    /**
     * Closes the connection
     */
    public void close() {
        try {
            channel.close();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Insert into SiriDB
     *
     * @param query
     * @param handler
     */
    public void insert(Map map, CompletionHandler handler) {
        channelWriter((byte) CPROTO_RES_INSERT, qpack.pack(map), handler);
    }

    /**
     * Perform a query on SiriDB
     *
     * @param query
     * @param handler
     */
    public void query(String query, CompletionHandler handler) {
        channelWriter((byte) CPROTO_REQ_QUERY, qpack.pack(new String[]{query, null}), handler);
    }

    /**
     * Perform a query on SiriDB with time precision
     *
     * @param query
     * @param timePrecision
     * @param handler
     */
    public void query(String query, int timePrecision, CompletionHandler handler) {
        channelWriter((byte) CPROTO_REQ_QUERY, qpack.pack(new String[]{query, timePrecision + ""}), handler);
    }
}
