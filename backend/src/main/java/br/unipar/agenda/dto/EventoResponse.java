package br.unipar.agenda.dto;

import br.unipar.agenda.model.Evento;
import java.time.LocalDate;

public record EventoResponse(Long id, String nome, LocalDate data, String local, Integer vagas, Integer inscritos) {

    public EventoResponse(Evento evento) {
        this(evento.getId(), evento.getNome(), evento.getData(), evento.getLocal(),
                evento.getVagas(), evento.getInscricoes().size());
    }
}
