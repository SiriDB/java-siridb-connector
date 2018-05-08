package transceptor.technology;

/**
 *
 * @author Tristan Nottelman
 */
public class ProtoMap {
    
    // SiriDB Client protocol client request types
    public static final int CPROTO_REQ_QUERY = 0;
    public static final int CPROTO_REQ_INSERT = 1;
    public static final int CPROTO_REQ_AUTH = 2;
    public static final int CPROTO_REQ_PING = 3;
    public static final int CPROTO_REQ_INFO = 4;
    public static final int CPROTO_REQ_LOADDB = 5;
    public static final int CPROTO_REQ_REGISTER_SERVER = 6;
    public static final int CPROTO_REQ_FILE_SERVERS = 7;
    public static final int CPROTO_REQ_FILE_USERS = 8;
    public static final int CPROTO_REQ_FILE_GROUPS = 9;
    
    // SiriDB Client protocol success server response types
    public static final int CPROTO_RES_QUERY = 0;
    public static final int CPROTO_RES_INSERT = 1;
    public static final int CPROTO_RES_AUTH_SUCCESS = 2;
    public static final int CPROTO_RES_ACK = 3;
    public static final int CPROTO_RES_INFO = 4;
    public static final int CPROTO_RES_FILE = 5;
}
