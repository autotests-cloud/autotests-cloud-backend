package cloud.autotests.backend.builders;

import cloud.autotests.backend.models.Order;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;

public class TestBuilder { // todo improve with files in resources
    private final String TEST_CLASS_TEMPLATE_PATH = "src/main/resources/github/AppTests.java.tpl";
    private final String TEST_STEP_TEMPLATE_PATH = "src/main/resources/github/step.tpl";

    public String generateTestClass(Order order) { // todo add link to Jira issue
        StringBuilder generatedSteps = new StringBuilder();
        String orderSteps = order.getSteps() // todo move do model ?
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\"", "\\\"");
        String[] steps = orderSteps.split("\n");
        for (String step : steps) {
            generatedSteps.append(generateStep(step));
        }

        return String.format(getTestClassTemplate(), order.getTitle(), generatedSteps);
    }

    private String generateStep(String step) {
        return String.format(getTestStepTemplate(), step);
    }

    private String getTestClassTemplate() {
        return readStringFromFile(TEST_CLASS_TEMPLATE_PATH);
    }

    private String getTestStepTemplate() {
        return readStringFromFile(TEST_STEP_TEMPLATE_PATH);
    }
}
