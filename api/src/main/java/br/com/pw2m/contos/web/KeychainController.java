package br.com.pw2m.contos.web;

import br.com.pw2m.contos.dto.ActivateRequest;
import br.com.pw2m.contos.dto.ActivationResponse;
import br.com.pw2m.contos.dto.KeychainPublicResponse;
import br.com.pw2m.contos.service.KeychainService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keychains")
public class KeychainController {

    private final KeychainService keychains;

    public KeychainController(KeychainService keychains) {
        this.keychains = keychains;
    }

    @GetMapping("/{publicCode}")
    public KeychainPublicResponse inspect(@PathVariable String publicCode) {
        return keychains.inspect(publicCode);
    }

    @PostMapping("/{publicCode}/activate")
    public ActivationResponse activate(@PathVariable String publicCode, @Valid @RequestBody ActivateRequest request) {
        return keychains.activate(publicCode, request.name(), request.email(), request.password(), request.pin());
    }
}
