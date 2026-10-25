package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Entitlement;
import br.com.pw2m.contos.domain.EntitlementSource;
import br.com.pw2m.contos.domain.Keychain;
import br.com.pw2m.contos.domain.KeychainStatus;
import br.com.pw2m.contos.domain.UserAccount;
import br.com.pw2m.contos.repository.EntitlementRepository;
import br.com.pw2m.contos.repository.KeychainRepository;
import br.com.pw2m.contos.repository.UserRepository;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class KeychainActivationStore {

    private final KeychainRepository keychains;
    private final UserRepository users;
    private final EntitlementRepository entitlements;
    private final ReadingService reading;
    private final PasswordEncoder encoder;

    public KeychainActivationStore(
            KeychainRepository keychains,
            UserRepository users,
            EntitlementRepository entitlements,
            ReadingService reading,
            PasswordEncoder encoder
    ) {
        this.keychains = keychains;
        this.users = users;
        this.entitlements = entitlements;
        this.reading = reading;
        this.encoder = encoder;
    }

    public record Completed(
            UUID userId,
            String name,
            String email,
            String storySlug,
            String storyTitle,
            String ticket
    ) {
    }

    @Transactional
    public Completed activate(UUID keychainId, String name, String email, String rawPassword) {
        Keychain keychain = keychains.findWithStoryById(keychainId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Chaveiro não encontrado."));
        if (keychain.getStatus() != KeychainStatus.NEW || keychain.getPinHash() == null) {
            throw new ApiException(HttpStatus.CONFLICT, "Este chaveiro já foi ativado. O acesso fica preso a ele e não pode ser transferido.");
        }

        UserAccount user = users.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            user = users.saveAndFlush(new UserAccount(UUID.randomUUID(), name, email, encoder.encode(rawPassword), Instant.now()));
        } else if (!encoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Este e-mail já tem conta. Use a senha dessa conta para vincular o chaveiro.");
        }

        keychain.activate(user, Instant.now());
        if (!entitlements.existsByUser_IdAndStory_Id(user.getId(), keychain.getStory().getId())) {
            entitlements.saveAndFlush(new Entitlement(
                    UUID.randomUUID(),
                    user,
                    keychain.getStory(),
                    EntitlementSource.KEYCHAIN,
                    keychain,
                    Instant.now()
            ));
        }
        String ticket = reading.issue(user.getId(), keychain.getStory().getId());
        return new Completed(
                user.getId(),
                user.getName(),
                user.getEmail(),
                keychain.getStory().getSlug(),
                keychain.getStory().getTitle(),
                ticket
        );
    }
}
