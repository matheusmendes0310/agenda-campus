package br.unipar.agenda.repository;

import br.unipar.agenda.model.Participante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    List<Participante> findAllByOrderByNomeAsc();

    boolean existsByEmailIgnoreCase(String email);

    // Participantes inscritos em um evento (usado em GET /api/eventos/{id}/participantes)
    List<Participante> findByInscricoesEventoIdOrderByNomeAsc(Long eventoId);
}
