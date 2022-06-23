package exceptions;

import java.sql.SQLException;

public class SqlRuntimeException extends RuntimeException {
    public SqlRuntimeException(SQLException message) {
        super(message);
    }

    public SqlRuntimeException() {
        super();
    }

    public SqlRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlRuntimeException(Throwable cause) {
        super(cause);
    }
}
