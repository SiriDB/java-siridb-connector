package transceptor.technology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transceptor.technology.ProtoMap.*;

class CompletionStore {

    public CompletionStore(CompletionHandler completionHandler, Object attachment) {
        this.completionHandler = completionHandler;
        this.attachment = attachment;
    }
    CompletionHandler completionHandler;
    Object attachment;
}

/**
 *
 * @author Tristan Nottelman
 */
public class Connection implements ConnectionInterface {

    private AsynchronousSocketChannel channel;
    private final String username;
    private final String password;
    private final String dbname;
    private final String host;
    private final int port;
    private short packageId;
    private final QPack qpack;

    private final Map<Short, CompletionStore> completionHandlers;
    private final boolean keepAlive;

    /**
     * Create a new instance of Connection with credentials, database name, host
     * and port
     *
     * @param username
     * @param password
     * @param dbname
     * @param host
     * @param port
     * @param keepAlive
     */
    public Connection(String username, String password, String dbname, String host, int port, boolean keepAlive) {
        this.channel = null;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.host = host;
        this.port = port;
        this.keepAlive = keepAlive;
        packageId = -1;
        qpack = new QPack();
        completionHandlers = new HashMap<>();
    }

    public boolean isConnected() {
        return channel.isOpen();
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
                    // checkbit
                    if (!p.isValid()) {
                        CompletionStore handler = completionHandlers.remove(p.getId());
                        handler.completionHandler.failed(new InvalidPackageException("Invalid package, received type "
                                + (((p.getType() + 256) ^ 255)) + " but checkbit was "
                                + (p.getCheckbit())), handler.attachment);
                        channelReader();
                        return;
                    }
                    if (p.getLength() == 0) {
                        // packet without body
                        handlePackage(p);
                        channelReader();
                        
                    } else {
                        bodyReader(p);
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("ChannelReader failed: " + exc.getMessage());
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
                Package p = (Package) attachment;

                if (result < 0) {
                    // end of stream
                    close();
                    CompletionStore handler = completionHandlers.remove(p.getId());
                    handler.completionHandler.failed(new Exception("End of stream"), handler.attachment);
                } else if (bodyBuffer.remaining() > 0) {
                    channel.read(bodyBuffer, p, this);
                } else {
                    p.setBody(bodyBuffer.array());
                    handlePackage(p);
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
    private void channelWriter(byte packageType, byte[] data, CompletionHandler handler, Object attachment) {
        packageId++;
        packageId = (short) (packageId % Short.MAX_VALUE);
        Package pck = new Package(data.length, packageId, packageType, data);

        completionHandlers.put(packageId, new CompletionStore(handler, attachment));

        channel.write(pck.toByteBuffer(), pck, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                System.out.printf("ChannelWrite: write %d bytes\n", result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                CompletionStore handler = completionHandlers.remove(((Package) attachment).getId());
                handler.completionHandler.failed(exc, handler.attachment);
            }
        });
    }

    private void handlePackage(Package p) {
        CompletionStore handler = completionHandlers.remove(p.getId());

        Object o = null;
        if (p.getLength() > 0) {
            try {
                o = qpack.unpack(p.getBody(), "utf-8");
            } catch (Exception e) {
                handler.completionHandler.failed(e, handler.attachment);
                return;
            }
        }

        if (p.getType() >= CPROTO_ERR_MSG) {
            ErrorFactory f = new ErrorFactory();
            handler.completionHandler.failed(f.getErrorException(p.getType(), o), handler.attachment);
        } else {
            handler.completionHandler.completed(o, handler.attachment);
        }
    }

    /**
     * Connect to SiriDB
     *
     * @param handler
     * @param attachment
     */
    @Override
    public void connect(CompletionHandler handler, Object attachment) {
        try {
            // connect to SiriDB
            channel = AsynchronousSocketChannel.open();
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, keepAlive);
            InetSocketAddress address = new InetSocketAddress(host, port);
            channel.connect(address, attachment, new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                    channelReader();
                    handler.completed(result, attachment);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    handler.failed(exc, attachment);
                }
            });
        } catch (IOException ex) {
            handler.failed(ex, null);
        }
    }

    /**
     * Authenticate
     *
     * @param handler
     * @param attachment
     */
    @Override
    public void authenticate(CompletionHandler handler, Object attachment) {
        channelWriter((byte) CPROTO_REQ_AUTH, qpack.pack(new String[]{username, password, dbname}), handler, attachment);
    }

    /**
     * Closes the connection
     */
    @Override
    public void close() {
        try {
            //channel.shutdownInput();
            channel.close();

        } catch (IOException ex) {
            Logger.getLogger(Connection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Insert into SiriDB
     *
     * @param map
     * @param handler
     * @param attachment
     */
    @Override
    public void insert(Map map, CompletionHandler handler, Object attachment) {
        channelWriter((byte) CPROTO_RES_INSERT, qpack.pack(map), handler, attachment);
    }

    /**
     * Perform a query on SiriDB
     *
     * @param query
     * @param handler
     * @param attachment
     */
    @Override
    public void query(String query, CompletionHandler handler, Object attachment) {
        channelWriter((byte) CPROTO_REQ_QUERY, qpack.pack(new String[]{query, null}), handler, attachment);
    }

    /**
     * Perform a query on SiriDB with time precision
     *
     * @param query
     * @param timePrecision
     * @param handler
     * @param attachment
     */
    @Override
    public void query(String query, int timePrecision, CompletionHandler handler, Object attachment) {
        channelWriter((byte) CPROTO_REQ_QUERY, qpack.pack(new String[]{query, timePrecision + ""}), handler, attachment);
    }
}
