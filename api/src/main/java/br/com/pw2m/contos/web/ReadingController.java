package br.com.pw2m.contos.web;

import br.com.pw2m.contos.config.AppProperties;
import br.com.pw2m.contos.service.ReadingService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;

@RestController
@RequestMapping("/api/reading")
public class ReadingController {

    private final ReadingService reading;
    private final AppProperties properties;

    public ReadingController(ReadingService reading, AppProperties properties) {
        this.reading = reading;
        this.properties = properties;
    }

    @GetMapping("/{token:.+}")
    public ResponseEntity<String> read(@PathVariable String token) {
        try {
            String html = Files.readString(reading.resolve(token));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                    .cacheControl(CacheControl.noStore())
                    .header("Content-Security-Policy", "frame-ancestors 'self' " + properties.getFrameAncestors())
                    .body(html);
        } catch (ApiException exception) {
            return page(exception.getStatus().value(), exception.getMessage());
        } catch (Exception exception) {
            return page(500, "Não foi possível abrir a história.");
        }
    }

    private ResponseEntity<String> page(int status, String message) {
        String safe = message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
                <title>História indisponível</title>
                <style>body{margin:0;min-height:100vh;display:grid;place-items:center;background:#142026;color:#f6efe2;font-family:Georgia,serif}
                main{max-width:28rem;padding:2rem;text-align:center}p{font-family:system-ui,sans-serif;line-height:1.5}</style>
                </head><body><main><h1>História indisponível</h1><p>%s</p></main></body></html>
                """.formatted(safe);
        return ResponseEntity.status(status)
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(html);
    }
}
