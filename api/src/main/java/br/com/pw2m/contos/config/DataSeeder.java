package br.com.pw2m.contos.config;

import br.com.pw2m.contos.domain.Keychain;
import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.repository.KeychainRepository;
import br.com.pw2m.contos.repository.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DataSeeder implements ApplicationRunner {

    public static final String DEMO_CODE = "PW2M-0001-DEMO";
    public static final String DEMO_PIN = "482913";

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final StoryRepository stories;
    private final KeychainRepository keychains;
    private final PasswordEncoder encoder;
    private final AppProperties properties;

    public DataSeeder(StoryRepository stories, KeychainRepository keychains, PasswordEncoder encoder, AppProperties properties) {
        this.stories = stories;
        this.keychains = keychains;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        Story patinho = ensureStory(
                "0001",
                "o-patinho-feio",
                "O Patinho Feio",
                "Hans Christian Andersen",
                "Um patinho diferente atravessa o inverno até encontrar o lugar onde é reconhecido. Adaptação ilustrada para ler no celular.",
                1990,
                "0001 - O_patinho_feio.html"
        );
        ensureStory(
                "0002",
                "chapeuzinho-vermelho",
                "Chapeuzinho Vermelho",
                "Irmãos Grimm",
                "A menina de capuz vermelho cruza a floresta para visitar a avó e aprende a desconfiar de quem finge a voz de quem ama.",
                1690,
                null
        );
        ensureStory(
                "0003",
                "os-tres-porquinhos",
                "Os Três Porquinhos",
                "Tradicional",
                "Três irmãos constroem casas bem diferentes e descobrem, na hora do lobo, o valor de fazer o trabalho com calma.",
                1490,
                null
        );
        ensureStory(
                "0004",
                "joao-e-maria",
                "João e Maria",
                "Irmãos Grimm",
                "Dois irmãos deixam um rastro na floresta, enfrentam a casa de doces e voltam para casa apoiados um no outro.",
                1790,
                null
        );
        if (keychains.findByPublicCode(DEMO_CODE).isEmpty()) {
            keychains.save(Keychain.fresh(
                    UUID.randomUUID(),
                    DEMO_CODE,
                    encoder.encode(DEMO_PIN),
                    patinho,
                    Instant.now()
            ));
            log.info("Chaveiro de demonstração disponível em {}/c/{}", properties.getPublicBaseUrl().replaceAll("/+$", ""), DEMO_CODE);
        }
    }

    private Story ensureStory(String code, String slug, String title, String author, String synopsis, int priceCents, String htmlFile) {
        return stories.findBySlug(slug).orElseGet(() -> stories.save(new Story(
                UUID.randomUUID(),
                code,
                slug,
                title,
                author,
                synopsis,
                priceCents,
                htmlFile
        )));
    }
}
