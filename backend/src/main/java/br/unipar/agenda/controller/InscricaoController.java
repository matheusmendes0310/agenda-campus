package br.unipar.agenda.controller;

import br.unipar.agenda.dto.InscricaoRequest;
import br.unipar.agenda.dto.InscricaoResponse;
import br.unipar.agenda.service.InscricaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// CRUD completo do recurso Inscrição
@RestController
@RequestMapping("/api/inscricoes")
@Tag(name = "Inscrições (CRUD)")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as inscrições")
    public List<InscricaoResponse> listar() {
        return inscricaoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma inscrição pelo ID")
    public InscricaoResponse buscarPorId(@PathVariable Long id) {
        return inscricaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma inscrição (somente ADMIN)")
    public InscricaoResponse criar(@RequestBody @Valid InscricaoRequest dados) {
        return inscricaoService.criar(dados);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma inscrição (somente ADMIN)")
    public InscricaoResponse atualizar(@PathVariable Long id, @RequestBody @Valid InscricaoRequest dados) {
        return inscricaoService.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui uma inscrição (somente ADMIN)")
    public void excluir(@PathVariable Long id) {
        inscricaoService.excluir(id);
    }
}
