package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JenkinsConfig {
    @Value("${jenkins.url}")
    public String jenkinsUrl;

    @Value("${jenkins.username}")
    public String jenkinsUsername;

    @Value("${jenkins.token}")
    public String jenkinsToken;
}
