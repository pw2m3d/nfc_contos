package br.com.pw2m.contos.service;

import br.com.pw2m.contos.config.AppProperties;
import br.com.pw2m.contos.domain.Keychain;
import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.dto.CreateStoryRequest;
import br.com.pw2m.contos.dto.CreatedKeychainResponse;
import br.com.pw2m.contos.dto.KeychainAdminItem;
import br.com.pw2m.contos.dto.StoryCard;
import br.com.pw2m.contos.dto.UpdateStoryRequest;
import br.com.pw2m.contos.repository.KeychainRepository;
import br.com.pw2m.contos.repository.StoryRepository;
import br.com.pw2m.contos.util.Codes;
import br.com.pw2m.contos.util.StoryFiles;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AdminService {

    private static final Pattern CODE = Pattern.compile("[A-Za-z0-9]{2,20}");
    private static final Pattern SLUG = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    private final StoryRepository stories;
    private final KeychainRepository keychains;
    private final PasswordEncoder encoder;
    private final AppProperties properties;

    public AdminService(
            StoryRepository stories,
            KeychainRepository keychains,
            PasswordEncoder encoder,
            AppProperties properties
    ) {
        this.stories = stories;
        this.keychains = keychains;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public List<StoryCard> stories() {
        return stories.findAllByOrderByCodeAsc().stream()
                .map(story -> StoryViews.card(story, false))
                .toList();
    }

    @Transactional
    public StoryCard createStory(CreateStoryRequest request) {
        String code = request.code().trim();
        String slug = request.slug().trim().toLowerCase();
        if (!CODE.matcher(code).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O código da história usa apenas letras e números.");
        }
        if (!SLUG.matcher(slug).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O identificador usa letras minúsculas, números e hífens.");
        }
        if (stories.findByCode(code).isPresent() || stories.findBySlug(slug).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe uma história com este código ou identificador.");
        }
        String htmlFile = blankToNull(request.htmlFile());
        if (htmlFile != null) {
            StoryFiles.checkName(htmlFile);
        }
        Story story = stories.save(new Story(
                UUID.randomUUID(),
                code,
                slug,
                request.title().trim(),
                request.author().trim(),
                request.synopsis().trim(),
                request.priceCents(),
                htmlFile
        ));
        return StoryViews.card(story, false);
    }

    @Transactional
    public StoryCard updateStory(String slug, UpdateStoryRequest request) {
        Story story = stories.findBySlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "História não encontrada."));
        if (request.priceCents() != null) {
            story.setPriceCents(request.priceCents());
        }
        if (request.htmlFile() != null) {
            String htmlFile = blankToNull(request.htmlFile());
            if (htmlFile != null) {
                StoryFiles.checkName(htmlFile);
            }
            story.setHtmlFile(htmlFile);
        }
        if (request.active() != null) {
            story.setActive(request.active());
        }
        return StoryViews.card(story, false);
    }

    @Transactional(readOnly = true)
    public List<KeychainAdminItem> keychains() {
        return keychains.findAllByOrderByCreatedAtDesc().stream()
                .map(keychain -> new KeychainAdminItem(
                        keychain.getPublicCode(),
                        keychain.getStatus().name(),
                        keychain.getStory().getTitle(),
                        keychain.getStory().getSlug(),
                        keychain.getOwner() == null ? null : keychain.getOwner().getEmail(),
                        keychain.getCreatedAt(),
                        keychain.getActivatedAt()
                ))
                .toList();
    }

    @Transactional
    public CreatedKeychainResponse createKeychain(String storySlug) {
        Story story = stories.findBySlug(storySlug.trim())
                .filter(Story::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "História não encontrada."));
        String pin = Codes.pin();
        String publicCode = Codes.publicCode(story.getCode());
        keychains.save(Keychain.fresh(
                UUID.randomUUID(),
                publicCode,
                encoder.encode(pin),
                story,
                Instant.now()
        ));
        return new CreatedKeychainResponse(publicCode, pin, nfcUrl(publicCode), story.getTitle());
    }

    private String nfcUrl(String publicCode) {
        String base = properties.getPublicBaseUrl().replaceAll("/+$", "");
        return base + "/c/" + publicCode;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
