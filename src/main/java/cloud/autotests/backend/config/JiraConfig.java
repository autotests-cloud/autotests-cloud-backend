package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JiraConfig {
    @Value("${jira.url}")
    public String jiraUrl;

    @Value("${jira.username}")
    public String jiraUsername;

    @Value("${jira.password}")
    public String jiraPassword;
}
