package cloud.autotests.backend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlConstraint {
    String message() default "Url not correct";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
