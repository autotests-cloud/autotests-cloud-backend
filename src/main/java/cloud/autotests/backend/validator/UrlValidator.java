package cloud.autotests.backend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlValidator implements
        ConstraintValidator<UrlConstraint, String> {

    @Override
    public void initialize(UrlConstraint contactNumber) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
