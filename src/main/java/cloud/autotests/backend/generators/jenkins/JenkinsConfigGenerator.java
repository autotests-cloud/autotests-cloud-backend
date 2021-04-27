package cloud.autotests.backend.generators.jenkins;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class JenkinsConfigGenerator {
    private static final String CONFIG_TEMPLATE_PATH = "src/main/resources/jenkins/config.xml.tpl";

    TelegramConfig telegramConfig;

    public String getConfig(Order order, String githubRepositoryUrl, Integer telegramChatMessageId) {
        String launchName = order.getTitle().replace("_", "-"); // todo remove bug in notifications

        return format(getConfigTemplate(), githubRepositoryUrl, "%s", "%s",
                telegramConfig.getToken(), telegramConfig.getChatId(), telegramChatMessageId,
                launchName, "prod", launchName); // todo add site
    }

    private String getConfigTemplate() {
        return readStringFromFile(CONFIG_TEMPLATE_PATH);
    }
}
