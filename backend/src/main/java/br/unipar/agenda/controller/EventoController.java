package br.unipar.agenda.controller;

import br.unipar.agenda.dto.EventoRequest;
import br.unipar.agenda.dto.EventoResponse;
import br.unipar.agenda.dto.ParticipanteResponse;
import br.unipar.agenda.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    @Operation(summary = "Lista eventos, com filtro opcional por nome e data (ex.: ?nome=Oficina&data=2026-12-03)")
    public List<EventoResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return eventoService.listar(nome, data);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um evento pelo ID")
    public EventoResponse buscarPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id);
    }

    @GetMapping("/{id}/participantes")
    @Operation(summary = "Lista os participantes inscritos em um evento")
    public List<ParticipanteResponse> listarParticipantes(@PathVariable Long id) {
        return eventoService.listarParticipantes(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um evento (somente ADMIN)")
    public EventoResponse criar(@RequestBody @Valid EventoRequest dados) {
        return eventoService.criar(dados);
    }
}
