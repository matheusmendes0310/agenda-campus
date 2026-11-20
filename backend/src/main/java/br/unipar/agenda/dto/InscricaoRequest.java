package br.unipar.agenda.dto;

import jakarta.validation.constraints.NotNull;

public record InscricaoRequest(
        @NotNull(message = "Selecione um evento.")
        Long eventoId,

        @NotNull(message = "Selecione um participante.")
        Long participanteId
) {
}
