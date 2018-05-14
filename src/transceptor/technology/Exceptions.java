package transceptor.technology;

/**
 *
 * @author Tristan Nottelman
 */

class QueryError extends Exception {
    public QueryError(String message) {
        super(message);
    }
}


class InsertError extends Exception {
    public InsertError(String message) {
        super(message);
    }
}


class ServerError extends Exception {
    public ServerError(String message) {
        super(message);
    }
}


class PoolError extends Exception {
    public PoolError(String message) {
        super(message);
    }
}


class AuthenticationError extends Exception {
    public AuthenticationError(String message) {
        super(message);
    }
}


class UserAuthError extends Exception {
    public UserAuthError(String message) {
        super(message);
    }
}

class CorruptPackage extends Exception {
    public CorruptPackage(String message) {
        super(message);
    }
}
