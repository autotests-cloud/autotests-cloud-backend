package cloud.autotests.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


public class AppTests extends TestBase {
    @Test
    @Description("Soon to be implemented by QA.GURU engineers")
    @DisplayName("Some test title")
    void generatedTest() {
        step("make tests", () -> {
            // todo
        });
    }

    @Test
    @Description("Soon to be implemented by QA.GURU engineers maybe tomorrow")
    @DisplayName("Some test title on not some")
    void generatedMoreTest() {
        step("make tests", () -> {
            // todo
        });
    }
}