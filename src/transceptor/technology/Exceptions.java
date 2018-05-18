package transceptor.technology;

/**
 *
 * @author Tristan Nottelman
 */

class QueryErrorException extends Exception {
    public QueryErrorException(String message) {
        super(message);
    }
}


class InsertErrorException extends Exception {
    public InsertErrorException(String message) {
        super(message);
    }
}


class ServerErrorException extends Exception {
    public ServerErrorException(String message) {
        super(message);
    }
}


class PoolErrorException extends Exception {
    public PoolErrorException(String message) {
        super(message);
    }
}


class AuthenticationErrorException extends Exception {
    public AuthenticationErrorException(String message) {
        super(message);
    }
}


class UserAuthErrorException extends Exception {
    public UserAuthErrorException(String message) {
        super(message);
    }
}

class InvalidPackageException extends Exception {
    public InvalidPackageException(String message) {
        super(message);
    }
}
