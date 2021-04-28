package cloud.autotests.backend.utils;

import cloud.autotests.backend.controllers.OrderController;
import cloud.autotests.backend.models.Order;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

public class CleanContentUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CleanContentUtils.class);


    public static String cleanText(String text) {
        String[] safeTexts = text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\"", "\\\"")
//                .replace("\\", "\\\\")
                .replace("{code}", "") // jira markdown
                .replace("</code>", "") //
                .replace("<code>", "")
                .split("\n");

        List<String> cleanSteps = new ArrayList<>();
        for (String step : safeTexts) {
            String trimmedStep = step.trim();
            if (trimmedStep.length() > 2)
                cleanSteps.add(trimmedStep);
        }
        return join("\n", cleanSteps);
    }

    public static String cutText(String text, int length) {
        return (text.length() > length) ? text.substring(0, length) : text;
    }

    public static String cutCleanText(String text, int length) {
        return cleanText(cutText(text, length));
    }

    public static Order cleanOrder(Order rawOrder) {
        LOG.info("\n[RAW ORDER]\n{}\n", rawOrder);

        Order cleanOrder = new Order();
        cleanOrder.setPrice(cutCleanText(rawOrder.getPrice(), 50));
        cleanOrder.setEmail(cutCleanText(rawOrder.getEmail(), 100));
        cleanOrder.setTitle(cutCleanText(rawOrder.getTitle(), 140));
        cleanOrder.setSteps(cutCleanText(rawOrder.getSteps(), 1500));
        LOG.info("\n[CLEAN ORDER]\n{}\n", cleanOrder);

        return cleanOrder;
    }
}

