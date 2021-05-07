package cloud.autotests.backend.utils;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlUtils.class);

    public static String getHtmlFromUrl(String url) {
        try {
            return String.valueOf(Unirest.get(url).asString().getBody());
        } catch (UnirestException e) {
            LOG.error("[URL NOT VALID] {}\n {}", url, e.getMessage());
        }
        return "";
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
