package br.unipar.agenda.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EventoRequest(
        @NotBlank(message = "O nome do evento é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotNull(message = "A data do evento é obrigatória.")
        LocalDate data,

        @NotBlank(message = "O local do evento é obrigatório.")
        @Size(max = 120, message = "O local deve ter no máximo 120 caracteres.")
        String local,

        @NotNull(message = "A quantidade de vagas é obrigatória.")
        @Min(value = 1, message = "O evento deve ter pelo menos 1 vaga.")
        Integer vagas
) {
}
