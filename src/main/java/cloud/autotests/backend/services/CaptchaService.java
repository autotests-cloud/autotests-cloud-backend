package cloud.autotests.backend.services;

import cloud.autotests.backend.captcha.GoogleResponse;
import cloud.autotests.backend.config.CaptchaConfig;
import cloud.autotests.backend.exceptions.ReCaptchaInvalidException;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class CaptchaService {

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    @Autowired
    private CaptchaConfig captchaConfig;

    public void processResponse(String response, String clientIp) {
        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }

        String validationUrl = format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                captchaConfig.getSecret(), response, clientIp);

        GoogleResponse googleResponse = Unirest.get(validationUrl).asObject(GoogleResponse.class).getBody();

        if (!googleResponse.isSuccess()) {
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}