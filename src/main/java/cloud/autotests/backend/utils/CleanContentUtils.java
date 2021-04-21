package cloud.autotests.backend.utils;

import cloud.autotests.backend.models.Order;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

public class CleanContentUtils {
    public static String cleanText(String text) {
        String[] safeTexts = text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("{code}", "")
                .replace("</code>", "")
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

    public static Order cleanOrder(Order order) {
        order.setPrice(cutCleanText(order.getPrice(), 50));
        order.setEmail(cutCleanText(order.getEmail(), 100));
        order.setTitle(cutCleanText(order.getTitle(), 140));
        order.setSteps(cutCleanText(order.getSteps(), 1500));

        return order;
    }
}

