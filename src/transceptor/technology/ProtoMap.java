package transceptor.technology;

/**
 *
 * @author Tristan Nottelman
 */
class ProtoMap {

    // SiriDB Client protocol client request types
    static final int CPROTO_REQ_QUERY = 0;
    static final int CPROTO_REQ_INSERT = 1;
    static final int CPROTO_REQ_AUTH = 2;
    static final int CPROTO_REQ_PING = 3;
    static final int CPROTO_REQ_INFO = 4;
    static final int CPROTO_REQ_LOADDB = 5;
    static final int CPROTO_REQ_REGISTER_SERVER = 6;
    static final int CPROTO_REQ_FILE_SERVERS = 7;
    static final int CPROTO_REQ_FILE_USERS = 8;
    static final int CPROTO_REQ_FILE_GROUPS = 9;

    // SiriDB Client protocol success server response types
    static final int CPROTO_RES_QUERY = 0;
    static final int CPROTO_RES_INSERT = 1;
    static final int CPROTO_RES_AUTH_SUCCESS = 2;
    static final int CPROTO_RES_ACK = 3;
    static final int CPROTO_RES_INFO = 4;
    static final int CPROTO_RES_FILE = 5;

    // SiriDB Client protocol error server response types
    static final int CPROTO_ERR_MSG = 64;
    static final int CPROTO_ERR_QUERY = 65;
    static final int CPROTO_ERR_INSERT = 66;
    static final int CPROTO_ERR_SERVER = 67;
    static final int CPROTO_ERR_POOL = 68;
    static final int CPROTO_ERR_USER_ACCESS = 69;
    static final int CPROTO_ERR = 70;
    static final int CPROTO_ERR_NOT_AUTHENTICATED = 71;
    static final int CPROTO_ERR_AUTH_CREDENTIALS = 72;
    static final int CPROTO_ERR_AUTH_UNKNOWN_DB = 73;
    static final int CPROTO_ERR_LOADING_DB = 74;
    static final int CPROTO_ERR_FILE = 75;
    static final int CPROTO_ERR_ADMIN = 96;
    static final int CPROTO_ERR_ADMIN_INVALID_REQUEST = 97; 
}
