package cloud.autotests.backend.exceptions;

import java.io.Serial;

public final class ReCaptchaInvalidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5861310537366287163L;

    private final String error;

    public ReCaptchaInvalidException(String error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return "Cant validate captcha: " + error;
    }
}