package br.unipar.agenda.controller;

import br.unipar.agenda.dto.ParticipanteRequest;
import br.unipar.agenda.dto.ParticipanteResponse;
import br.unipar.agenda.service.ParticipanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
@Tag(name = "Participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @GetMapping
    @Operation(summary = "Lista os participantes")
    public List<ParticipanteResponse> listar() {
        return participanteService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um participante (somente ADMIN)")
    public ParticipanteResponse criar(@RequestBody @Valid ParticipanteRequest dados) {
        return participanteService.criar(dados);
    }
}
