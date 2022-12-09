package cloud.autotests.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({BadRequestException.class, ReCaptchaInvalidException.class})
    public ResponseEntity<ErrorResponse> badRequestException(Exception exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CreateObjectException.class, ServerException.class})
    public ResponseEntity<ErrorResponse> alreadyExistsException(Exception exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
