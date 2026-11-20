package br.unipar.agenda.repository;

import br.unipar.agenda.model.Evento;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Evento> findEventoById(Long id);

    List<Evento> findAllByOrderByDataAsc();

    List<Evento> findByNomeContainingIgnoreCaseOrderByDataAsc(String nome);

    List<Evento> findByDataOrderByNomeAsc(LocalDate data);

    List<Evento> findByNomeContainingIgnoreCaseAndDataOrderByNomeAsc(String nome, LocalDate data);
}
