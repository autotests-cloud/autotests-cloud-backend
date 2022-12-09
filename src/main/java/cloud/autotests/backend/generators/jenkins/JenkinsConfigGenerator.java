package cloud.autotests.backend.generators.jenkins;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.request.Opts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class JenkinsConfigGenerator {
    private static final String CONFIG_TEMPLATE_PATH = "src/main/resources/jenkins/config.xml.tpl";
    private final TelegramConfig telegramConfig;

    public String getConfig(@NotNull Opts opts, String githubRepositoryUrl, Integer telegramChatMessageId, String title) {
        String launchName = title.replace("_", "-"); // todo remove bug in notifications

        return format(getConfigTemplate(), githubRepositoryUrl,
                telegramConfig.getToken(), telegramConfig.getChatId(), telegramChatMessageId,
                launchName, "prod", launchName); // todo add site
    }

    private String getConfigTemplate() {
        return readStringFromFile(CONFIG_TEMPLATE_PATH);
    }
}
