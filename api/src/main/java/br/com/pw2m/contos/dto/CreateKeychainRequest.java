package br.com.pw2m.contos.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateKeychainRequest(
        @NotBlank(message = "Escolha a história do chaveiro.")
        String storySlug
) {
}
