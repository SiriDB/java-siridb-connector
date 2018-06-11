package transceptor.technology;

import static transceptor.technology.ProtoMap.*;

/**
 *
 * @author Tristan Nottelman
 */
public class ErrorFactory {
    public Exception getErrorException(int type, String msg) {
        switch (type) {
            case CPROTO_ERR_MSG:
                return new Exception(msg);
            case CPROTO_ERR_QUERY:
                return new QueryErrorException(msg);
            case CPROTO_ERR_INSERT:
                return new InsertErrorException(msg);
            case CPROTO_ERR_SERVER:
                return new ServerErrorException(msg);
            case CPROTO_ERR_POOL:
                return new PoolErrorException(msg);
            case CPROTO_ERR_USER_ACCESS:
                return new AuthenticationErrorException(msg);
            case CPROTO_ERR:
                return new ServerErrorException(msg);
            case CPROTO_ERR_NOT_AUTHENTICATED:
                return new AuthenticationErrorException(msg);
            case CPROTO_ERR_AUTH_CREDENTIALS:
                return new UserAuthErrorException(msg);
            case CPROTO_ERR_AUTH_UNKNOWN_DB:
                return new UserAuthErrorException(msg);
            case CPROTO_ERR_LOADING_DB:
                return new ServerErrorException(msg);
            case CPROTO_ERR_FILE:
                return new ServerErrorException(msg);
            default:
                return new Exception(msg);
        }
    }
}
