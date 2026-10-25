package br.com.pw2m.contos.util;

import br.com.pw2m.contos.web.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoryFilesTest {

    @TempDir
    Path root;

    @Test
    void resolvesTheExampleStoryInsideTheStoriesFolder() throws Exception {
        Path file = root.resolve("0001 - O_patinho_feio.html");
        Files.writeString(file, "<html></html>");

        assertEquals(file.toAbsolutePath().normalize(), StoryFiles.resolve(root, "0001 - O_patinho_feio.html"));
    }

    @Test
    void refusesAPathThatLeavesTheStoriesFolder() {
        assertThrows(ApiException.class, () -> StoryFiles.resolve(root, "../segredo.html"));
    }
}
