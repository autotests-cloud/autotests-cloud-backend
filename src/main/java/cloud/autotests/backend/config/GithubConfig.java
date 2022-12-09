package cloud.autotests.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ConfigurationProperties(prefix = "github")
public class GithubConfig {

    public static final String API_TEMPLATE_REPOSITORY_URL = "https://api.github.com/repos/%s/%s/generate";
    public static final String API_NEW_TEST_CLASS_PATH = "https://api.github.com/repos/%s/%s/contents/src/test/java/cloud/autotests/tests/%sTests.java";
    public static final String NEW_TEST_CLASS_SHORTENED_URL = "github.com/.../tests/%sTests.java";

    @Value("${github.token}")
    public String githubToken;

    @Value("${github.template.owner}")
    public String githubTemplateOwner;

    @Value("${github.template.repository}")
    public String githubTemplateRepository;

    @Value("${github.generated.owner}")
    public String githubGeneratedOwner;
}
