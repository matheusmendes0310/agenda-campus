package br.unipar.agenda.dto;

import br.unipar.agenda.model.Inscricao;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InscricaoResponse(
        Long id,
        LocalDateTime dataInscricao,
        Long eventoId,
        String eventoNome,
        LocalDate eventoData,
        String eventoLocal,
        Long participanteId,
        String participanteNome,
        String participanteEmail
) {

    public InscricaoResponse(Inscricao inscricao) {
        this(inscricao.getId(),
                inscricao.getDataInscricao(),
                inscricao.getEvento().getId(),
                inscricao.getEvento().getNome(),
                inscricao.getEvento().getData(),
                inscricao.getEvento().getLocal(),
                inscricao.getParticipante().getId(),
                inscricao.getParticipante().getNome(),
                inscricao.getParticipante().getEmail());
    }
}
