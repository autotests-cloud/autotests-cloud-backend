package cloud.autotests.backend.builders;

import cloud.autotests.backend.models.Order;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;

public class TestBuilder { // todo improve with files in resources
//    private final String TEST_CLASS_TEMPLATE_PATH = "github/AppTests.java.tpl";
//    private final String TEST_STEP_TEMPLATE_PATH = "github/step.tpl";
//
public String generateTestClass(Order order) { // todo add link to Jira issue
        StringBuilder generatedSteps = new StringBuilder();
        String orderSteps = order.getSteps() // todo move do model ?
                .replace("\r\n", "\n")
                .replace("\r", "\n");
        String[] steps = orderSteps.split("\n");
        for (String step: steps) {
            generatedSteps.append(generateStep(step));
        }

        return String.format(getTestClassTemplate(), order.getTitle(), generatedSteps);
    }

    private String generateStep(String step) {
        return String.format(getTestStepTemplate(), step);
    }

    private String getTestClassTemplate() {
        return "package cloud.autotests.tests;\n" +
                "\n" +
                "import io.qameta.allure.*;\n" +
                "import org.junit.jupiter.api.DisplayName;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "import static io.qameta.allure.Allure.step;\n" +
                "\n" +
                "@Epic(\"any\")\n" +
                "@Feature(\"your\")\n" +
                "@Story(\"metadata\")\n" +
                "public class AppTests extends TestBase {\n" +
                "\n" +
                "    @Test\n" +
                "    @Description(\"Soon to be implemented by QA.GURU engineers\")\n" +
                "    @DisplayName(\"%s\")\n" +
                "    void test() {\n" +
                "%s\n" +
                "    }\n" +
                "}";
    }

    private String getTestStepTemplate() {
        return "\n" +
                "        step(\"%s\", () -> {\n" +
                "            // todo\n" +
                "        });\n";
    }
}
