package cloud.autotests.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsConfig {

    public static final String JOB_URL_TEMPLATE = "%s/job/%s";
    public static final String API_CREATE_JOB_URL_TEMPLATE = "%s/createItem?name=%s";
    public static final String API_LAUNCH_JOB_URL_TEMPLATE = "%s/job/%s/buildWithParameters?delay=1sec";
    public static final String API_JOB_STATUS_URL_TEMPLATE = "%s/job/%s/1/api/json";


    @Value("${jenkins.url}")
    public String url;

    @Value("${jenkins.username}")
    public String username;

    @Value("${jenkins.token}")
    public String token;

}
