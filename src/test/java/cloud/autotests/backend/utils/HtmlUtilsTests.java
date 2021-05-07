package cloud.autotests.backend.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cloud.autotests.backend.utils.HtmlUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void getHtmlFromUrlTest() {
        assertThat(getHtmlFromUrl("https://google.com")).isNotEmpty();
    }

    @Test
    void getHtmlFromWrongUrlTest() {
        assertThat(getHtmlFromUrl("http://wrong.url")).isEmpty();
    }

    @Test
    void getHtmlFromInvalidUrlTest() {
        assertThat(getHtmlFromUrl("afvwfas")).isEmpty();
    }

    @Test
    void getTitleValueTest() {
        assertThat(title).isEqualTo("Google");
    }

    @Test
    void getTitleValueEmptyTest() throws IOException {
        File input = new File("./src/test/resources/empty_title.html");
        Document htmlDom = Jsoup.parse(input, "UTF-8");
        title = getTitleValue(htmlDom);

        assertThat(title).isEmpty();
    }

    @Test
    void getHeaderValuesToStringContentTest() {
        assertThat(headers.toString()).isEqualTo("{h1=[h1 here, h1 with class here], h2=[h2 here]}");
    }

    @Test
    void getHeaderValueMapContentTest() {
        assertThat(headers.get("h1").get(0)).isEqualTo("h1 here");
        assertThat(headers.get("h1").get(1)).isEqualTo("h1 with class here");
        assertThat(headers.get("h2").get(0)).isEqualTo("h2 here");
    }
}
