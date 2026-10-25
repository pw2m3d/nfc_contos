package br.com.pw2m.contos.dto;

public record ActivationResponse(
        String token,
        String name,
        String email,
        String storySlug,
        String storyTitle,
        String ticket
) {
}
