package cloud.autotests.backend.exceptions;

import java.io.Serial;

public class ServerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5825208464257254683L;
    private final String message;

    public ServerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
