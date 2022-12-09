package cloud.autotests.backend.generators.tests;

import org.junit.jupiter.api.Test;

import static cloud.autotests.backend.generators.tests.TestClassStructureGenerator.*;
import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClassStructureGeneratorTests {

    private static final String TEST_CLASS_EXAMPLE_PATH = "src/test/resources/github/test_class.tpl";
    private static final String TEST_CLASS_TWO_TESTS_EXAMPLE_PATH = "src/test/resources/github/test_class_two_tests.tpl";
    private static final String TEST_METHOD_EXAMPLE_PATH = "src/test/resources/github/test_method.tpl";
    private static final String TEST_METHOD_TWO_STEPS_EXAMPLE_PATH = "src/test/resources/github/test_method_two_steps.tpl";
    private static final String TEST_STEP_EXAMPLE_PATH = "src/test/resources/github/test_step.tpl";
    private static final String TEST_STEP_ONE_LINE_EXAMPLE_PATH = "src/test/resources/github/test_step_one_line.tpl";
    private static final String testClassPrefix = "App";
    private static final String testMethodDescription = "Soon to be implemented by QA.GURU engineers";
    private static final String testMethodNamePrefix = "generated";
    private static final String stepDescription = "make tests";
    private static final String stepContent = "// todo";
    private static final String price = "free";
    private static final String email = "a@a.a";
    private static final String title = "Some test title";
    private static final String steps = "make tests\nmake more\nnot war";
    private static final String captcha = "no";

    @Test
    void generateTestClassTest() {
        String generatedTestStep = generateTestStep(stepDescription, stepContent);
        String generatedTestMethod = generateTestMethod(
                testMethodDescription, title,
                testMethodNamePrefix, generatedTestStep);

        String generatedTestClass = generateTestClass(
                testClassPrefix, generatedTestMethod);
        String expectedTestClass = readStringFromFile(TEST_CLASS_EXAMPLE_PATH);

        assertEquals(expectedTestClass, generatedTestClass);
    }

    @Test
    void generateTestClassWithTwoTestsTest() {
        String generatedTestStep = generateTestStep(stepDescription, stepContent);
        String generatedTestMethods = generateTestMethod(
                testMethodDescription, title,
                testMethodNamePrefix, generatedTestStep);
        generatedTestMethods += generateTestMethod(
                testMethodDescription + " maybe tomorrow",
                title + " on not some",
                testMethodNamePrefix + "More", generatedTestStep);

        String generatedTestClass = generateTestClass(
                testClassPrefix, generatedTestMethods);
        String expectedTestClass = readStringFromFile(TEST_CLASS_TWO_TESTS_EXAMPLE_PATH);

        assertEquals(expectedTestClass, generatedTestClass);
    }

    @Test
    void generateTestMethodTest() {
        String generatedTestStep = generateTestStep(stepDescription, stepContent);
        String generatedTestMethod = generateTestMethod(
                testMethodDescription, title,
                testMethodNamePrefix, generatedTestStep);
        String expectedTestMethod = readStringFromFile(TEST_METHOD_EXAMPLE_PATH);

        assertEquals(expectedTestMethod, generatedTestMethod);
    }

    @Test
    void generateTestMethodWithTwoStepsTest() {
        String generatedTestSteps = generateTestStep(stepDescription, stepContent);
        generatedTestSteps += generateTestStepOneLine(
                stepDescription + " not war",stepContent + " or not todo");

        String generatedTestMethod = generateTestMethod(
                testMethodDescription, title,
                testMethodNamePrefix, generatedTestSteps);
        String expectedTestMethod = readStringFromFile(TEST_METHOD_TWO_STEPS_EXAMPLE_PATH);

        assertEquals(expectedTestMethod, generatedTestMethod);
    }

    @Test
    void generateTestStepTest() {
        String generatedTestStep = generateTestStep(stepDescription, stepContent);
        String expectedTestStep = readStringFromFile(TEST_STEP_EXAMPLE_PATH);

        assertEquals(expectedTestStep, generatedTestStep);
    }

    @Test
    void generateTestStepOneLineTest() {
        String generatedTestStep = generateTestStepOneLine(stepDescription, stepContent);
        String expectedTestStep = readStringFromFile(TEST_STEP_ONE_LINE_EXAMPLE_PATH);

        assertEquals(expectedTestStep, generatedTestStep);
    }
}
