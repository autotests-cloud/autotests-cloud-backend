package cloud.autotests.backend.generators.tests;

import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static java.lang.String.format;

public class TestClassStructureGenerator {
    private static final String TEST_CLASS_TEMPLATE_PATH = "src/main/resources/github/test_class.tpl";
    private static final String TEST_METHOD_TEMPLATE_PATH = "src/main/resources/github/test_method.tpl";
    private static final String TEST_STEP_TEMPLATE_PATH = "src/main/resources/github/test_step.tpl";
    private static final String TEST_STEP_ONE_LINE_TEMPLATE_PATH = "src/main/resources/github/test_step_one_line.tpl";


    public static String generateTestClass(String testClassPrefix, String testClassContent) {
        return format(readStringFromFile(TEST_CLASS_TEMPLATE_PATH),
                testClassPrefix, testClassContent);
    }

    public static String generateTestMethod(String testDescription, String testDisplayName,
                                            String testMethodNamePrefix, String testContent) {
        return format(readStringFromFile(TEST_METHOD_TEMPLATE_PATH),
                testDescription, testDisplayName,
                testMethodNamePrefix, testContent);
    }

    public static String generateTestStep(String stepDescription, String stepContent) {
        return format(readStringFromFile(TEST_STEP_TEMPLATE_PATH),
                stepDescription, stepContent);
    }

    public static String generateTestStepOneLine(String stepDescription, String stepContent) {
        return format(readStringFromFile(TEST_STEP_ONE_LINE_TEMPLATE_PATH),
                stepDescription, stepContent);
    }

}
