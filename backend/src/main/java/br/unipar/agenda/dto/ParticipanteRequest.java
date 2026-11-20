package br.unipar.agenda.dto;

import jakarta.validation.constraints.*;

public record ParticipanteRequest(
        @NotBlank(message = "O nome do participante é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String email
) {
}
