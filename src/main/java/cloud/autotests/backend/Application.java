package cloud.autotests.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableWebMvc
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
