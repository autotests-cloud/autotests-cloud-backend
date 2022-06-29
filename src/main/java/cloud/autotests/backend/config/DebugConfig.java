package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DebugConfig {

    @Value("${debug.mode}")
    private Boolean debugMode;

}