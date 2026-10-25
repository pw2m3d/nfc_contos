package br.com.pw2m.contos.web;

import br.com.pw2m.contos.dto.AuthResponse;
import br.com.pw2m.contos.dto.LoginRequest;
import br.com.pw2m.contos.dto.MeResponse;
import br.com.pw2m.contos.security.CurrentUser;
import br.com.pw2m.contos.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public MeResponse me() {
        return auth.me(CurrentUser.id());
    }
}
