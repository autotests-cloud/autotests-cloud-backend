package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.String.format;

@Configuration
@Data
public class GithubConfig {

    public static final String TEMPLATE_REPOSITORY_URL = "https://api.github.com/repos/%s/%s/generate";
    public static final String NEW_TEST_REPOSITORY_PATH = "https://api.github.com/repos/%s/%s/contents/" +
            "src/test/java/cloud/autotests/tests/%sTests.java";

    @Value("${github.token}")
    public String githubToken;

    @Value("${github.template.owner}")
    public String githubTemplateOwner;

    @Value("${github.template.repository}")
    public String githubTemplateRepository;

    @Value("${github.generated.owner}")
    public String githubGeneratedOwner;
}
