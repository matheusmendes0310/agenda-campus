package br.unipar.agenda.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Informe o usuário.")
        String login,

        @NotBlank(message = "Informe a senha.")
        String senha
) {
}
