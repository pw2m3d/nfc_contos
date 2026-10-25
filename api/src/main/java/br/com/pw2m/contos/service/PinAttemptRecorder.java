package br.com.pw2m.contos.service;

import br.com.pw2m.contos.repository.KeychainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PinAttemptRecorder {

    private final KeychainRepository keychains;

    public PinAttemptRecorder(KeychainRepository keychains) {
        this.keychains = keychains;
    }

    @Transactional
    public int incrementAndGet(UUID keychainId) {
        int updated = keychains.incrementPinAttempts(keychainId);
        if (updated == 0) {
            return -1;
        }
        return keychains.findById(keychainId)
                .orElseThrow()
                .getPinAttempts();
    }
}
