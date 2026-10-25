package br.com.pw2m.contos.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String storySlug,
        String storyTitle,
        int amountCents,
        String status,
        Instant createdAt,
        Instant paidAt
) {
}
