package br.com.pw2m.contos.dto;

public record CreatedKeychainResponse(
        String publicCode,
        String pin,
        String nfcUrl,
        String storyTitle
) {
}
