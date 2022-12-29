package cloud.autotests.backend.utils;

import cloud.autotests.backend.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class Utils {
    public static byte[] readBytesFromFile(String filePath) {
        File file = new File(filePath);
        try {
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static String readStringFromFile(String filePath) {
        return new String(readBytesFromFile(filePath), UTF_8);
    }

    public static String getAuthority(String url) {

        log.info("getAuthority with {}", url);
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error("getAuthority " + url, e);
            throw new BadRequestException(e.getMessage());
        }
        String authority = uri.getAuthority();
        log.info("getAuthority result {}", authority);

        return authority;
    }
}
