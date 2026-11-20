package br.unipar.agenda.service;

import br.unipar.agenda.dto.EventoRequest;
import br.unipar.agenda.dto.EventoResponse;
import br.unipar.agenda.dto.ParticipanteResponse;
import br.unipar.agenda.exception.RecursoNaoEncontradoException;
import br.unipar.agenda.exception.RegraNegocioException;
import br.unipar.agenda.model.Evento;
import br.unipar.agenda.repository.EventoRepository;
import br.unipar.agenda.repository.ParticipanteRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public EventoService(EventoRepository eventoRepository, ParticipanteRepository participanteRepository) {
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    // Consulta com múltiplos parâmetros: GET /api/eventos?nome=...&data=...
    @Transactional(readOnly = true)
    public List<EventoResponse> listar(String nome, LocalDate data) {
        boolean temNome = nome != null && !nome.isBlank();
        List<Evento> eventos;

        if (temNome && data != null) {
            eventos = eventoRepository.findByNomeContainingIgnoreCaseAndDataOrderByNomeAsc(nome.trim(), data);
        } else if (temNome) {
            eventos = eventoRepository.findByNomeContainingIgnoreCaseOrderByDataAsc(nome.trim());
        } else if (data != null) {
            eventos = eventoRepository.findByDataOrderByNomeAsc(data);
        } else {
            eventos = eventoRepository.findAllByOrderByDataAsc();
        }

        return eventos.stream().map(EventoResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse buscarPorId(Long id) {
        return new EventoResponse(buscarEntidade(id));
    }

    // Consulta com filtro no path: GET /api/eventos/{id}/participantes
    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listarParticipantes(Long eventoId) {
        buscarEntidade(eventoId); // devolve 404 se o evento não existir
        return participanteRepository.findByInscricoesEventoIdOrderByNomeAsc(eventoId).stream()
                .map(ParticipanteResponse::new)
                .toList();
    }

    @Transactional
    public EventoResponse criar(EventoRequest dados) {
        // Regra de negócio: não cadastrar evento com data no passado
        if (dados.data().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data do evento não pode estar no passado.");
        }
        Evento evento = new Evento(dados.nome().trim(), dados.data(), dados.local().trim(), dados.vagas());
        return new EventoResponse(eventoRepository.save(evento));
    }

    public Evento buscarParaInscricao(Long id) {
        return eventoRepository.findEventoById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado."));
    }

    public Evento buscarEntidade(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado."));
    }
}
