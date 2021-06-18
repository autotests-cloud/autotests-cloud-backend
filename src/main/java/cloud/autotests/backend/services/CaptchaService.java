package cloud.autotests.backend.services;

import cloud.autotests.backend.captcha.GoogleResponse;
import cloud.autotests.backend.config.CaptchaConfig;
import cloud.autotests.backend.exceptions.ReCaptchaInvalidException;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class CaptchaService {
    private static final Logger LOG = LoggerFactory.getLogger(CaptchaService.class);

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Autowired
    private CaptchaConfig captchaConfig;

    public void processResponse(String captcha) {
        if (!responseSanityCheck(captcha)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }

        String validationUrl = format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                captchaConfig.getSecret(), captcha);
        LOG.info("reCaptcha url request: {}", validationUrl);

        GoogleResponse googleResponse = Unirest.get(validationUrl).asObject(GoogleResponse.class).getBody();
        LOG.info("reCaptcha response: {}", googleResponse.toString());

        if (!googleResponse.isSuccess()) {
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}