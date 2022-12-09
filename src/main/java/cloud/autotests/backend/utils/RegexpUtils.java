package cloud.autotests.backend.utils;

import cloud.autotests.backend.models.request.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {

    private static final Pattern pattern = Pattern.compile("((http|https)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&amp;:/~+#-]*[\\w@?^=%&amp;/~+#-])?)");

    public static List<String> getUrlsFromOrder(List<Test> manuals) {
        List<String> urls = new ArrayList<>();
        for (Test step : manuals) {
            Matcher m = pattern.matcher(step.getStep());
            if (m.find()) {
                urls.add(m.group(1));
            }
        }
        return urls;
    }
}
