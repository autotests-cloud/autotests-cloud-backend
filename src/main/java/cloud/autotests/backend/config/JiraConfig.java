package cloud.autotests.backend.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@Data
public class JiraConfig {
    @Value("${jira.url}")
    public String url;

    @Value("${jira.project.key}")
    public String projectKey;

    @Value("${jira.username}")
    public String username;

    @Value("${jira.password}")
    public String password;

    @Bean
    public JiraRestClient jiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getUri(), this.username, this.password);
    }

    private URI getUri() {
        return URI.create(this.url);
    }
}
