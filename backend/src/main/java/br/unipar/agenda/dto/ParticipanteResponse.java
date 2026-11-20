package br.unipar.agenda.dto;

import br.unipar.agenda.model.Participante;

public record ParticipanteResponse(Long id, String nome, String email) {

    public ParticipanteResponse(Participante participante) {
        this(participante.getId(), participante.getNome(), participante.getEmail());
    }
}
