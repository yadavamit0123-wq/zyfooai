package com.pt.zyfooai;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LocaleStringsXmlTest {

    private static final Pattern BARE_AMPERSAND = Pattern.compile("[^;]&[^a-zA-Z#]");
    private static final Pattern DOUBLE_ESCAPED_APOSTROPHE = Pattern.compile("\\\\\\\\'");

    @Test
    public void localeStrings_haveValidEscapes() throws IOException {
        Path resDir = Paths.get("src/main/res");
        try (Stream<Path> paths = Files.walk(resDir)) {
            paths.filter(p -> p.toString().matches(".*/values-[^/]+/strings\\.xml$"))
                    .forEach(LocaleStringsXmlTest::assertValidLocaleStrings);
        }
    }

    private static void assertValidLocaleStrings(Path path) {
        try {
            String content = Files.readString(path);
            assertFalse(
                    path + " contains \\\\' (use \\' for apostrophes in Android strings)",
                    DOUBLE_ESCAPED_APOSTROPHE.matcher(content).find()
            );
            assertFalse(
                    path + " contains unescaped & (use &amp;)",
                    BARE_AMPERSAND.matcher(content).find()
            );
            assertTrue(path + " should contain string resources", content.contains("<string name="));
        } catch (IOException e) {
            throw new AssertionError("Failed to read " + path, e);
        }
    }
}
