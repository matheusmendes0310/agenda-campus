package br.unipar.agenda.service;

import br.unipar.agenda.dto.InscricaoRequest;
import br.unipar.agenda.dto.InscricaoResponse;
import br.unipar.agenda.exception.RecursoNaoEncontradoException;
import br.unipar.agenda.exception.RegraNegocioException;
import br.unipar.agenda.model.Evento;
import br.unipar.agenda.model.Inscricao;
import br.unipar.agenda.model.Participante;
import br.unipar.agenda.repository.InscricaoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoService eventoService;
    private final ParticipanteService participanteService;

    public InscricaoService(InscricaoRepository inscricaoRepository, EventoService eventoService,
                            ParticipanteService participanteService) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoService = eventoService;
        this.participanteService = participanteService;
    }

    @Transactional(readOnly = true)
    public List<InscricaoResponse> listar() {
        return inscricaoRepository.findAllByOrderByIdDesc().stream()
                .map(InscricaoResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public InscricaoResponse buscarPorId(Long id) {
        return new InscricaoResponse(buscarEntidade(id));
    }

    @Transactional
    public InscricaoResponse criar(InscricaoRequest dados) {
        // A transação segura o bloqueio até concluir o cadastro.
        Evento evento = eventoService.buscarParaInscricao(dados.eventoId());
        Participante participante = participanteService.buscarEntidade(dados.participanteId());

        validarInscricao(evento, participante, true);

        Inscricao inscricao = new Inscricao(evento, participante);
        return new InscricaoResponse(inscricaoRepository.save(inscricao));
    }

    @Transactional
    public InscricaoResponse atualizar(Long id, InscricaoRequest dados) {
        Inscricao inscricao = buscarParaAlterar(id);
        Evento evento = eventoService.buscarParaInscricao(dados.eventoId());
        Participante participante = participanteService.buscarEntidade(dados.participanteId());

        boolean mudouEvento = !evento.getId().equals(inscricao.getEvento().getId());
        boolean mudouParticipante = !participante.getId().equals(inscricao.getParticipante().getId());

        // Só valida de novo se a inscrição realmente mudou
        if (mudouEvento || mudouParticipante) {
            validarInscricao(evento, participante, mudouEvento);
        }

        inscricao.setEvento(evento);
        inscricao.setParticipante(participante);
        return new InscricaoResponse(inscricaoRepository.save(inscricao));
    }

    @Transactional
    public void excluir(Long id) {
        inscricaoRepository.delete(buscarParaAlterar(id));
    }

    // Regras de negócio da inscrição
    private void validarInscricao(Evento evento, Participante participante, boolean ocupaNovaVaga) {
        if (evento.getData().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("Não é possível se inscrever em um evento que já aconteceu.");
        }
        if (inscricaoRepository.existsByEventoIdAndParticipanteId(evento.getId(), participante.getId())) {
            throw new RegraNegocioException("Este participante já está inscrito neste evento.");
        }
        if (ocupaNovaVaga && inscricaoRepository.countByEventoId(evento.getId()) >= evento.getVagas()) {
            throw new RegraNegocioException("Não há mais vagas para este evento.");
        }
    }

    private Inscricao buscarParaAlterar(Long id) {
        return inscricaoRepository.findInscricaoById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada."));
    }

    private Inscricao buscarEntidade(Long id) {
        return inscricaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição não encontrada."));
    }
}
