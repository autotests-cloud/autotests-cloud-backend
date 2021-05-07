package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CaptchaConfig {

    @Value("${google.recaptcha.key.site}")
    private String site;
    @Value("${google.recaptcha.key.secret}")
    private String secret;

}