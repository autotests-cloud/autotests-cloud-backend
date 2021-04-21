package cloud.autotests.backend.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {

    public static Document getHtmlDom(String url) {
        Document htmlDom;
        try {
            htmlDom = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return htmlDom; // todo remove iframe
    }

    public static Map<String, List<String>> getHeaderValues(Document htmlDom) {
        Map<String, List<String>> collectedElements = new HashMap<>();
        htmlDom.removeAttr("iframe");
        Elements htmlElements = htmlDom.select("h1, h2, h3, h4, h5, h6");

        for (Element htmlElement : htmlElements) {
            if (htmlElement.hasText()) {
                collectedElements.computeIfAbsent(htmlElement.tagName(), k -> new ArrayList<>());
                collectedElements.get(htmlElement.tagName()).add(htmlElement.text());
            }
        }

        return collectedElements;
    }

    public static String getTitleValue(Document htmlDom) {
        return htmlDom.title();
    }
}
