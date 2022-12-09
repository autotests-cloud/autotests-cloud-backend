package cloud.autotests.backend.exceptions;

import java.io.Serial;

public class BadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5825208464257254680L;
    private final String object;

    public BadRequestException(String object) {
        this.object = object;
    }

    @Override
    public String getMessage() {
        return "BadData " + object;
    }
}
