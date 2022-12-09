package cloud.autotests.backend.validator;

import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.invoke.MethodHandles;
import java.util.Set;

public class ListNotEmptyValidator implements ConstraintValidator<ListNotEmpty, Set<String>> {
    private  static final Log LOG = LoggerFactory.make( MethodHandles.lookup() );

    private int min;
    private int max;

    @Override
    public void initialize(ListNotEmpty parameters) {
        min = parameters.min();
        max = parameters.max();
        validateParameters();
    }

    @Override
    public boolean isValid(Set<String> values, ConstraintValidatorContext context) {
        if ( values == null ) {
            return true;
        }
        return values.stream().noneMatch(value -> value.length() < min || value.length() > max);
    }

    private void validateParameters() {
        if ( min < 0 ) {
            throw LOG.getMinCannotBeNegativeException();
        }
        if ( max < 0 ) {
            throw LOG.getMaxCannotBeNegativeException();
        }
        if ( max < min ) {
            throw LOG.getLengthCannotBeNegativeException();
        }
    }
}
