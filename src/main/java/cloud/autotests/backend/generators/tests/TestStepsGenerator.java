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
        String stepDescription = format("Checking that page title is '%s'", title);
        String stepContent = format(
                "String expectedTitle = \"%s\";\r\n" +
                "            String actualTitle = title();\r\n" +
                "\r\n" +
                "            assertThat(actualTitle).isEqualTo(expectedTitle);", title);

        return generateTestStep(stepDescription, stepContent);
    }

}
