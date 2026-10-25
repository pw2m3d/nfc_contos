package br.com.pw2m.contos.dto;

public record StoryCard(
        String slug,
        String code,
        String title,
        String author,
        String synopsis,
        int priceCents,
        boolean published,
        boolean owned
) {
}
