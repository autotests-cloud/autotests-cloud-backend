package cloud.autotests.backend.utils;

import cloud.autotests.backend.services.JenkinsService;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlUtils.class);

    public static Document getHtmlFromUrl(String url) throws IOException {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            LOG.warn("Cannot connect to URL " + url);
            throw e;
        }
    }

    public static Document getHtmlDom(String url) {
        Document htmlDom;
        Connection urlConnect = Jsoup.connect(url);
        try {
            htmlDom = urlConnect.get();
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
