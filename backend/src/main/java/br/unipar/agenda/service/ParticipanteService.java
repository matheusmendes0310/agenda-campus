package br.unipar.agenda.service;

import br.unipar.agenda.dto.ParticipanteRequest;
import br.unipar.agenda.dto.ParticipanteResponse;
import br.unipar.agenda.exception.RecursoNaoEncontradoException;
import br.unipar.agenda.exception.RegraNegocioException;
import br.unipar.agenda.model.Participante;
import br.unipar.agenda.repository.ParticipanteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public List<ParticipanteResponse> listar() {
        return participanteRepository.findAllByOrderByNomeAsc().stream()
                .map(ParticipanteResponse::new)
                .toList();
    }

    @Transactional
    public ParticipanteResponse criar(ParticipanteRequest dados) {
        String email = dados.email().trim().toLowerCase();

        // Regra de negócio: o e-mail não pode se repetir
        if (participanteRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("Já existe um participante com esse e-mail.");
        }
        Participante participante = new Participante(dados.nome().trim(), email);
        return new ParticipanteResponse(participanteRepository.save(participante));
    }

    public Participante buscarEntidade(Long id) {
        return participanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Participante não encontrado."));
    }
}
