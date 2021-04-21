package cloud.autotests.backend.generators.jenkins;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static java.lang.String.format;

public class JenkinsConfigGenerator {
    private final String CONFIG_TEMPLATE_PATH = "src/main/resources/jenkins/config.xml.tpl";

    public String getConfig(Order order, TelegramConfig telegramConfig, String githubRepositoryUrl, Integer telegramChatMessageId) {
        String launchName = order.getTitle().replace("_", "-"); // todo remove bug in notifications

        return format(getConfigTemplate(), githubRepositoryUrl, "%s", "%s",
                telegramConfig.getTelegramToken(), telegramConfig.getTelegramChatId(), telegramChatMessageId,
                launchName, "prod", launchName); // todo add site
    }

    private String getConfigTemplate() {
        return readStringFromFile(CONFIG_TEMPLATE_PATH);
    }
}
