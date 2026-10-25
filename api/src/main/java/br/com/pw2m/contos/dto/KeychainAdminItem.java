package br.com.pw2m.contos.dto;

import java.time.Instant;

public record KeychainAdminItem(
        String publicCode,
        String status,
        String storyTitle,
        String storySlug,
        String ownerEmail,
        Instant createdAt,
        Instant activatedAt
) {
}
