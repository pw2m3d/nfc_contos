package br.com.pw2m.contos.dto;

public record LibraryItem(
        String slug,
        String code,
        String title,
        String author,
        String synopsis,
        String source,
        boolean published
) {
}
