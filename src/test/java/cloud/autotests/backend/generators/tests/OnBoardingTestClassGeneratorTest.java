package cloud.autotests.backend.generators.tests;

import cloud.autotests.backend.models.request.Test;
import cloud.autotests.backend.models.request.Tests;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class OnBoardingTestClassGeneratorTest {

    @org.junit.jupiter.api.Test
    void generateOnBoardingTestClass() {

        String result = OnBoardingTestClassGenerator.generateOnBoardingTestClass(Tests.builder()
                        .consoleCheck(true)
                        .titleCheck(true)
                        .test(List.of(
                                Test.builder()
                                        .title("title 1")
                                        .step("step1")
                                        .build(),
                                Test.builder()
                                        .title("title 2")
                                        .step("step2")
                                        .build(),
                                Test.builder()
                                        .title("title http")
                                        .step("step http://test.com")
                                        .build(),
                                Test.builder()
                                        .title("title https")
                                        .step("step https://tests.com")
                                        .build()
                        ))
                        .build(),
                "Generated",
                "title");

        // todo create chrcker
    }
}