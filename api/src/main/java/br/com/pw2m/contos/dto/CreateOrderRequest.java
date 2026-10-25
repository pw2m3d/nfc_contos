package br.com.pw2m.contos.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank(message = "Escolha uma história.")
        String storySlug
) {
}
