package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JenkinsConfig {

    public static final String CREATE_JOB_URL = "%s/createItem?name=%s";
    public static final String LAUNCH_JOB_URL = "%s/job/%s/buildWithParameters";


    @Value("${jenkins.url}")
    public String url;

    @Value("${jenkins.username}")
    public String username;

    @Value("${jenkins.token}")
    public String token;

}
