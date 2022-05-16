package cloud.autotests.backend.generators.tests;

import cloud.autotests.backend.models.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static cloud.autotests.backend.generators.tests.OnBoardingTestClassGenerator.generateFromUrlTestMethods;
import static cloud.autotests.backend.generators.tests.OnBoardingTestClassGenerator.generateOnBoardingTestClass;
import static cloud.autotests.backend.generators.tests.TestClassStructureGenerator.*;
import static cloud.autotests.backend.utils.CleanContentUtils.cleanOrder;
import static cloud.autotests.backend.utils.Utils.readStringFromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnBoardingTestClassGeneratorTests {

    static Order order;
    private static final String TEST_CLASS_ONBOARDING_PATH = "src/test/resources/github/test_class_onboarding.tpl";
    private static final String TEST_METHOD_ONBOARDING_FROM_URL_PATH = "src/test/resources/github/test_method_onboarding_from_url.tpl";
    private static final String testClassPrefix = "App";
    private static final String price = "free";
    private static final String email = "a@a.a";
    private static final String title = "Some test title";
    private static final String steps = "make tests https://selenide.org\nmake more\nnot war";
    private static final String captcha = "no";

    @BeforeAll
    static void initContent() {
        Order rawOrder = new Order();
        rawOrder.setPrice(price);
        rawOrder.setEmail(email);
        rawOrder.setTitle(title);
        rawOrder.setSteps(steps);
        rawOrder.setCaptcha(captcha);
        order = cleanOrder(rawOrder);
    }

    @Test
    void generateOnBoardingTestClassTest() {
        String generatedTestClass = generateOnBoardingTestClass(testClassPrefix, order);
        String expectedTestClass = readStringFromFile(TEST_CLASS_ONBOARDING_PATH);

        assertThat(generatedTestClass).isEqualTo(expectedTestClass);
    }

    @Test
    void generateFromUrlTestMethodsTest() {
        String generatedTestMethods = generateFromUrlTestMethods(order);
        String expectedTestMethods = readStringFromFile(TEST_METHOD_ONBOARDING_FROM_URL_PATH);

        assertThat(generatedTestMethods).isEqualTo(expectedTestMethods);
    }

    @Test
    void generateFromWrongUrlTestMethodsTest() {
        Order newOrder = order;
        newOrder.setSteps("Open https://some.sit\n" +
                "I click on logo\n" +
                "Logo should fly");
        String generatedTestMethods = generateFromUrlTestMethods(order);

        assertThat(generatedTestMethods).isEmpty();
    }


}
