package cloud.autotests.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ConfigurationProperties(prefix = "captcha")
public class CaptchaConfig {

    @Value("${google.recaptcha.key.site}")
    private String site;
    @Value("${google.recaptcha.key.secret}")
    private String secret;
    @Value("${google.recaptcha.enable}")
    private boolean check;
}