package cloud.autotests.backend.generators.tests;

import static cloud.autotests.backend.generators.tests.TestClassStructureGenerator.generateTestStep;
import static cloud.autotests.backend.generators.tests.TestClassStructureGenerator.generateTestStepOneLine;
import static java.lang.String.format;

public class TestStepsGenerator {
    public static String generateOpenPageStep(String url) {
        String stepDescription = format("Open url '%s'", url);
        String stepContent = format("open(\"%s\")", url);

        return generateTestStepOneLine(stepDescription, stepContent);
    }

    public static String generateCheckTitleStep(String title) {
        String stepDescription = format("Page title should have text '%s'", title);
        String stepContent = format(
                "String expectedTitle = \"%s\";\r\n" +
                "            String actualTitle = title();\r\n" +
                "\r\n" +
                "            assertThat(actualTitle).isEqualTo(expectedTitle);", title);

        return generateTestStep(stepDescription, stepContent);
    }

    public static String generateCheckConsoleErrorStep() {
        String stepDescription = "Console logs should not contain text 'SEVERE'";
        String stepContent =
                "String consoleLogs = DriverUtils.getConsoleLogs();\r\n" +
                "            String errorText = \"SEVERE\";\r\n" +
                "\r\n" +
                "            assertThat(consoleLogs).doesNotContain(errorText);";

        return generateTestStep(stepDescription, stepContent);
    }

    /*
            step("Page should not have errors (SEVERE) in console", () -> {
            String consoleLogs = getConsoleLogs();
            assertThat(consoleLogs).doesNotContain("SEVERE");
        });
     */
}
