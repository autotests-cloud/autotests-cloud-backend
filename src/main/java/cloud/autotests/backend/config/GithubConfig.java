package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GithubConfig {
    @Value("${github.token}")
    public String githubToken;

    @Value("${github.template.repository}")
    public String githubTemplateRepository;

    @Value("${github.template.generate.owner}")
    public String githubTemplateGenerateOwner;
}
