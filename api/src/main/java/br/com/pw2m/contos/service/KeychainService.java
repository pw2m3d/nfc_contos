package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Keychain;
import br.com.pw2m.contos.domain.KeychainStatus;
import br.com.pw2m.contos.dto.ActivationResponse;
import br.com.pw2m.contos.dto.KeychainPublicResponse;
import br.com.pw2m.contos.repository.KeychainRepository;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeychainService {

    static final int MAX_PIN_ATTEMPTS = 5;

    private final KeychainRepository keychains;
    private final PinAttemptRecorder pinAttempts;
    private final KeychainActivationStore store;
    private final ReadingService reading;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public KeychainService(
            KeychainRepository keychains,
            PinAttemptRecorder pinAttempts,
            KeychainActivationStore store,
            ReadingService reading,
            PasswordEncoder encoder,
            JwtService jwt
    ) {
        this.keychains = keychains;
        this.pinAttempts = pinAttempts;
        this.store = store;
        this.reading = reading;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public KeychainPublicResponse inspect(String publicCode) {
        Keychain keychain = find(publicCode);
        String ticket = null;
        if (keychain.getStatus() == KeychainStatus.ACTIVATED) {
            ticket = reading.issue(keychain.getOwner().getId(), keychain.getStory().getId());
        }
        return new KeychainPublicResponse(
                keychain.getPublicCode(),
                keychain.getStatus().name(),
                StoryViews.card(keychain.getStory(), false),
                ticket
        );
    }

    public ActivationResponse activate(String publicCode, String name, String email, String password, String pin) {
        Keychain keychain = find(publicCode.trim());
        if (keychain.getStatus() != KeychainStatus.NEW) {
            throw new ApiException(HttpStatus.CONFLICT, "Este chaveiro já foi ativado. O acesso fica preso a ele e não pode ser transferido.");
        }
        if (keychain.getPinAttempts() >= MAX_PIN_ATTEMPTS) {
            throw new ApiException(HttpStatus.LOCKED, "O PIN foi bloqueado depois de várias tentativas. Procure o suporte com o código do chaveiro.");
        }
        if (keychain.getPinHash() == null || !encoder.matches(pin, keychain.getPinHash())) {
            int attempts = pinAttempts.incrementAndGet(keychain.getId());
            if (attempts < 0) {
                throw new ApiException(HttpStatus.CONFLICT, "Este chaveiro já foi ativado. O acesso fica preso a ele e não pode ser transferido.");
            }
            if (attempts >= MAX_PIN_ATTEMPTS) {
                throw new ApiException(HttpStatus.LOCKED, "PIN incorreto. O chaveiro foi bloqueado depois de várias tentativas.");
            }
            throw new ApiException(HttpStatus.UNAUTHORIZED, "PIN incorreto. Ele está impresso na embalagem e só funciona neste chaveiro.");
        }

        KeychainActivationStore.Completed completed = store.activate(
                keychain.getId(),
                name.trim(),
                email.trim().toLowerCase(),
                password
        );
        return new ActivationResponse(
                jwt.issue(completed.userId(), completed.name(), completed.email()),
                completed.name(),
                completed.email(),
                completed.storySlug(),
                completed.storyTitle(),
                completed.ticket()
        );
    }

    private Keychain find(String publicCode) {
        return keychains.findByPublicCode(publicCode.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chaveiro não encontrado."));
    }
}
