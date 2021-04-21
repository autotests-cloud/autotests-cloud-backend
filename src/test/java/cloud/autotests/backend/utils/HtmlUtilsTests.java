package cloud.autotests.backend.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cloud.autotests.backend.utils.HtmlUtils.getHeaderValues;
import static cloud.autotests.backend.utils.HtmlUtils.getTitleValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlUtilsTests {

    static String title;
    static Map<String, List<String>> headers;

    @BeforeAll
    static void readHtml() throws IOException {
        File input = new File("./src/test/resources/index.html");
        Document htmlDom = Jsoup.parse(input, "UTF-8");
        title = getTitleValue(htmlDom);
        headers = getHeaderValues(htmlDom);
    }

    @Test
    void getTitleValueTest() {
        assertEquals(title, "Google");
    }

    @Test
    void getTitleValueEmptyTest() throws IOException {
        File input = new File("./src/test/resources/empty_title.html");
        Document htmlDom = Jsoup.parse(input, "UTF-8");
        title = getTitleValue(htmlDom);
        assertTrue(title.isEmpty());
    }

    @Test
    void getHeaderValuesToStringContentTest() {
        assertEquals(headers.toString(), "{h1=[h1 here, h1 with class here], h2=[h2 here]}");
    }

    @Test
    void getHeaderValueMapContentTest() {
        assertEquals(headers.get("h1").get(0), "h1 here");
        assertEquals(headers.get("h1").get(1), "h1 with class here");
        assertEquals(headers.get("h2").get(0), "h2 here");
    }
}
