package br.com.pw2m.contos.util;

import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.util.regex.Pattern;

public final class StoryFiles {

    private static final Pattern NAME = Pattern.compile("[0-9A-Za-z][0-9A-Za-z ._-]*\\.html");

    private StoryFiles() {
    }

    public static void checkName(String htmlFile) {
        if (htmlFile == null || htmlFile.contains("..") || !NAME.matcher(htmlFile).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O nome do arquivo deve ser um HTML na pasta de histórias, sem pastas. Exemplo: 0001 - O_patinho_feio.html");
        }
    }

    public static Path resolve(Path root, String htmlFile) {
        checkName(htmlFile);
        Path base = root.toAbsolutePath().normalize();
        Path target = base.resolve(htmlFile).normalize();
        if (!target.startsWith(base)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "O arquivo desta história não está publicado.");
        }
        return target;
    }
}
