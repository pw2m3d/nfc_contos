package br.com.pw2m.contos.dto;

import jakarta.validation.constraints.Min;

public record UpdateStoryRequest(
        @Min(value = 0, message = "O preço não pode ser negativo.")
        Integer priceCents,
        String htmlFile,
        Boolean active
) {
}
