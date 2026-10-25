package br.com.pw2m.contos.service;

import br.com.pw2m.contos.domain.UserAccount;
import br.com.pw2m.contos.dto.AuthResponse;
import br.com.pw2m.contos.dto.MeResponse;
import br.com.pw2m.contos.repository.UserRepository;
import br.com.pw2m.contos.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        UserAccount user = users.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos."));
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
        }
        return new AuthResponse(jwt.issue(user.getId(), user.getName(), user.getEmail()), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public MeResponse me(java.util.UUID userId) {
        UserAccount user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Faça login para continuar."));
        return new MeResponse(user.getName(), user.getEmail());
    }
}
