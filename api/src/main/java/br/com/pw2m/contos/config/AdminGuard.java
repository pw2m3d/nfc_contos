package br.com.pw2m.contos.config;

import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminGuard {

    private final AppProperties properties;

    public AdminGuard(AppProperties properties) {
        this.properties = properties;
    }

    public void check(String presented) {
        String expected = properties.getAdminKey();
        if (expected == null || expected.isBlank() || presented == null
                || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), presented.getBytes(StandardCharsets.UTF_8))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Chave de administração inválida.");
        }
    }
}
