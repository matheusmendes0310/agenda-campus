package br.unipar.agenda.controller;

import br.unipar.agenda.dto.LoginRequest;
import br.unipar.agenda.dto.TokenResponse;
import br.unipar.agenda.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Faz login e devolve o token JWT")
    public TokenResponse login(@RequestBody @Valid LoginRequest dados) {
        return authService.login(dados);
    }
}
