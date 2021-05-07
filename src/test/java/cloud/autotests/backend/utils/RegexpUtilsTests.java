package cloud.autotests.backend.utils;

import org.junit.jupiter.api.Test;

import static cloud.autotests.backend.utils.RegexpUtils.getUrlsFromOrder;
import static org.junit.jupiter.api.Assertions.*;

public class RegexpUtilsTests {
    private final String orderTextWithHttpUrl = "Open http://bash.im\nstep 1\nstep 2\nstep 3";
    private final String orderTextWithHttpsUrl = "Open https://bash.im\nstep 1\nstep 2\nstep 3";
    private final String orderTextWithoutUrl = "Open \nstep 1\nstep 2\nstep 3";

    @Test
    public void getUrlsFromOrderShouldReturnNotEmptyHttpListTest() {
        assertEquals(getUrlsFromOrder(orderTextWithHttpUrl).size(), 1);
    }

    @Test
    public void getUrlsFromOrderShouldReturnNotEmptyHttpsListTest() {
        assertEquals(getUrlsFromOrder(orderTextWithHttpsUrl).size(), 1);
    }

    @Test
    public void getUrlsFromOrderShouldReturnHttpsExactHttpUrlTest() {
        assertEquals("http://bash.im", getUrlsFromOrder(orderTextWithHttpUrl).get(0));
    }

    @Test
    public void getUrlsFromOrderShouldReturnHttpsExactHttpsUrlTest() {
        assertEquals("https://bash.im", getUrlsFromOrder(orderTextWithHttpsUrl).get(0));
    }

    @Test
    public void getUrlsFromOrderShouldReturnEmptyListTest() {
        assertEquals(getUrlsFromOrder(orderTextWithoutUrl).size(), 0);
    }

    /*
    <title>Google</title>
                    <h1>h1 here</h1>
                <h1 class="some class">h1 with class here</h1>
                <h2 class="some class">h2 here</h2>
     */
}
