package br.com.pw2m.contos.dto;

public record KeychainPublicResponse(
        String publicCode,
        String status,
        StoryCard story,
        String ticket
) {
}
