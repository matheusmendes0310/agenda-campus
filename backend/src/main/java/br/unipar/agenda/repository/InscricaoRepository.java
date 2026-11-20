package br.unipar.agenda.repository;

import br.unipar.agenda.model.Inscricao;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inscricao> findInscricaoById(Long id);

    List<Inscricao> findAllByOrderByIdDesc();

    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);

    long countByEventoId(Long eventoId);
}
