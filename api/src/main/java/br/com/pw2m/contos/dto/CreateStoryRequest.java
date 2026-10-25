package br.com.pw2m.contos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoryRequest(
        @NotBlank(message = "Informe o código da história.")
        @Size(min = 2, max = 20, message = "O código deve ter entre 2 e 20 caracteres.")
        String code,
        @NotBlank(message = "Informe o identificador da história.")
        @Size(min = 3, max = 80, message = "O identificador deve ter entre 3 e 80 caracteres.")
        String slug,
        @NotBlank(message = "Informe o título.")
        @Size(max = 160, message = "O título é longo demais.")
        String title,
        @NotBlank(message = "Informe o autor.")
        @Size(max = 160, message = "O autor é longo demais.")
        String author,
        @NotBlank(message = "Informe uma sinopse.")
        String synopsis,
        @Min(value = 0, message = "O preço não pode ser negativo.")
        int priceCents,
        String htmlFile
) {
}
