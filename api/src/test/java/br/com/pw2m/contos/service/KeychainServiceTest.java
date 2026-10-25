package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.Keychain;
import br.com.pw2m.contos.domain.KeychainStatus;
import br.com.pw2m.contos.domain.Story;
import br.com.pw2m.contos.repository.KeychainRepository;
import br.com.pw2m.contos.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KeychainServiceTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private KeychainRepository keychains;
    private PinAttemptRecorder pinAttempts;
    private KeychainActivationStore store;
    private KeychainService service;
    private Keychain fresh;

    @BeforeEach
    void setUp() {
        keychains = mock(KeychainRepository.class);
        pinAttempts = mock(PinAttemptRecorder.class);
        store = mock(KeychainActivationStore.class);
        service = new KeychainService(keychains, pinAttempts, store, mock(ReadingService.class), encoder, mock(JwtService.class));
        Story story = new Story(UUID.randomUUID(), "0001", "o-patinho-feio", "O Patinho Feio", "Andersen", "Sinopse", 1990, "0001 - O_patinho_feio.html");
        fresh = Keychain.fresh(UUID.randomUUID(), "PW2M-0001-DEMO", encoder.encode("482913"), story, Instant.now());
        when(keychains.findByPublicCode("PW2M-0001-DEMO")).thenReturn(Optional.of(fresh));
    }

    @Test
    void rejectsASecondActivation() {
        fresh.activate(new br.com.pw2m.contos.domain.UserAccount(UUID.randomUUID(), "Ana", "ana@exemplo.com", "hash", Instant.now()), Instant.now());

        ApiException error = assertThrows(ApiException.class, () ->
                service.activate("PW2M-0001-DEMO", "Bia", "bia@exemplo.com", "senha-forte", "482913"));

        assertEquals(HttpStatus.CONFLICT, error.getStatus());
        assertEquals(KeychainStatus.ACTIVATED, fresh.getStatus());
        verify(store, never()).activate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void countsAWrongPinAndDoesNotOpenTheStory() {
        when(pinAttempts.incrementAndGet(fresh.getId())).thenReturn(1);

        ApiException error = assertThrows(ApiException.class, () ->
                service.activate("PW2M-0001-DEMO", "Ana", "ana@exemplo.com", "senha-forte", "000000"));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
        verify(store, never()).activate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void opensTheIncludedStoryAfterTheRightPin() {
        when(store.activate(fresh.getId(), "Ana", "ana@exemplo.com", "senha-forte"))
                .thenReturn(new KeychainActivationStore.Completed(
                        UUID.randomUUID(), "Ana", "ana@exemplo.com", "o-patinho-feio", "O Patinho Feio", "ticket"));
        JwtService jwt = mock(JwtService.class);
        when(jwt.issue(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("Ana"), org.mockito.ArgumentMatchers.eq("ana@exemplo.com")))
                .thenReturn("sessao");
        service = new KeychainService(keychains, pinAttempts, store, mock(ReadingService.class), encoder, jwt);

        var response = service.activate("PW2M-0001-DEMO", " Ana ", "Ana@Exemplo.com", "senha-forte", "482913");

        assertEquals("sessao", response.token());
        assertEquals("o-patinho-feio", response.storySlug());
        assertEquals("ticket", response.ticket());
    }
}
