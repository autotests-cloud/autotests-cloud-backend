package cloud.autotests.backend.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;

public class Utils { // todo not working with docker
    public static File readFileFromClassPath(String filePath) throws IOException {
        return new ClassPathResource(filePath).getFile();
    }

    public static String readStringFromFile(String filePath) {
        try {
            return readFileToString(readFileFromClassPath(filePath), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
