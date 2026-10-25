package br.com.pw2m.contos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActivateRequest(
        @NotBlank(message = "Informe seu nome.")
        @Size(min = 2, max = 120, message = "O nome deve ter entre 2 e 120 letras.")
        String name,
        @NotBlank(message = "Informe seu e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 180, message = "O e-mail é longo demais.")
        String email,
        @NotBlank(message = "Informe uma senha.")
        @Size(min = 8, max = 80, message = "A senha deve ter entre 8 e 80 caracteres.")
        String password,
        @NotBlank(message = "Informe o PIN da embalagem.")
        @Pattern(regexp = "\\d{6}", message = "O PIN tem 6 números.")
        String pin
) {
}
