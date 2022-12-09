package cloud.autotests.backend.utils;

import cloud.autotests.backend.models.request.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static cloud.autotests.backend.utils.RegexpUtils.getUrlsFromOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegexpUtilsTests {
    private final String orderTextWithHttpUrl = "Open http://bash.im\nstep 1\nstep 2\nstep 3";
    private final String orderTextWithHttpsUrl = "Open https://bash.im\nstep 1\nstep 2\nstep 3";
    private final String orderTextWithoutUrl = "Open \nstep 1\nstep 2\nstep 3";

    private static Stream<Arguments> stepsProvider() {
        return Stream.of(
                Arguments.of("http",
                        List.of(Test.builder()
                                        .step("Open http://bash.im")
                                        .build(),
                                Test.builder()
                                        .step("step 1")
                                        .build(),
                                Test.builder()
                                        .step("step 2")
                                        .build()
                        ),
                        1),
                Arguments.of("https",
                        List.of(Test.builder()
                                        .step("Open https://bash.im")
                                        .build(),
                                Test.builder()
                                        .step("step 1")
                                        .build(),
                                Test.builder()
                                        .step("step 2")
                                        .build()
                        ),
                        1),
                Arguments.of("http and https",
                        List.of(Test.builder()
                                        .step("Open http://bash.im")
                                        .build(),
                                Test.builder()
                                        .step("step 1")
                                        .build(),
                                Test.builder()
                                        .step("Open https://bash.im end")
                                        .build()
                        ),
                        2),
                Arguments.of("not url",
                        List.of(Test.builder()
                                        .step("step 0")
                                        .build(),
                                Test.builder()
                                        .step("step 1")
                                        .build(),
                                Test.builder()
                                        .step("step 2")
                                        .build()
                        ),
                        0),
                Arguments.of("all url",
                        List.of(Test.builder()
                                        .step("Open http://bash.im")
                                        .build(),
                                Test.builder()
                                        .step("https://bash.im end")
                                        .build(),
                                Test.builder()
                                        .step("Open https://bash.im end")
                                        .build()
                        ),
                        3)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("stepsProvider")
    public void getUrlsFromOrderShouldReturnResultCount(String condition, List<Test> steps, int result) {
        assertEquals(getUrlsFromOrder(steps).size(), result);
    }

    @org.junit.jupiter.api.Test()
    @DisplayName("Verify upl")
    public void getUrlsFromOrderShouldReturnResultUrl() {
       List<String> result =  getUrlsFromOrder(List.of(Test.builder()
                        .step("Open http://bash.im")
                        .build(),
                Test.builder()
                        .step("https://bash.im end")
                        .build(),
                Test.builder()
                        .step("Open https://bash.im end")
                        .build()
        ));

       assertTrue(result.contains("http://bash.im"), "Containt in: Open http://bash.im");
       assertTrue(result.contains("https://bash.im"), "Containt in: https://bash.im end");
       assertTrue(result.contains("https://bash.im"), "Containt in: Open https://bash.im end");
    }
}
