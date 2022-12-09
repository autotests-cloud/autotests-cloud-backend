package cloud.autotests.backend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListNotEmptyValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListNotEmpty {
    String message() default "{javax.validation.constraints.Size.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 0;
    int max() default Integer.MAX_VALUE;
}
