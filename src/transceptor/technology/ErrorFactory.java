package transceptor.technology;

import java.util.Map;
import static transceptor.technology.ProtoMap.*;

/**
 *
 * @author Tristan Nottelman
 */
class ErrorFactory {
    Exception getErrorException(int type, Object o) {
        switch (type) {
            case CPROTO_ERR_MSG:
                return new Exception((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_QUERY:
                return new QueryErrorException((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_INSERT:
                return new InsertErrorException((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_SERVER:
                return new ServerErrorException((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_POOL:
                return new PoolErrorException((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_USER_ACCESS:
                return new AuthenticationErrorException((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR:
                return new Exception("Runtime error (" + type + ")");
            case CPROTO_ERR_NOT_AUTHENTICATED:
                return new Exception("Not authenticated (" + type + ")");
            case CPROTO_ERR_AUTH_CREDENTIALS:
                return new Exception("Invalid credentials (" + type + ")");
            case CPROTO_ERR_AUTH_UNKNOWN_DB:
                return new Exception("Unknown database (" + type + ")");
            case CPROTO_ERR_LOADING_DB:
                return new Exception("Error loading database (" + type + ")");
            case CPROTO_ERR_FILE:
                return new Exception("Error while downloading file (" + type + ")");
            case CPROTO_ERR_ADMIN:
                return new Exception((String) ((Map) o).get("error_msg"));
            case CPROTO_ERR_ADMIN_INVALID_REQUEST:
                return new Exception("Invalid request (" + type + ")");
            default:
                return new Exception("Unknown package type: " + type);
        }
    }
}
