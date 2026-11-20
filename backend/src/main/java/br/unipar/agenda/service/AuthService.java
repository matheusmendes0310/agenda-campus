package br.unipar.agenda.service;

import br.unipar.agenda.dto.LoginRequest;
import br.unipar.agenda.dto.TokenResponse;
import br.unipar.agenda.model.Usuario;
import br.unipar.agenda.repository.UsuarioRepository;
import br.unipar.agenda.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public TokenResponse login(LoginRequest dados) {
        // Confere usuário e senha (BCrypt). Se estiver errado, lança BadCredentialsException -> 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dados.login().trim(), dados.senha()));

        Usuario usuario = usuarioRepository.findByLogin(dados.login().trim())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha incorretos."));

        String token = tokenService.gerarToken(usuario);
        return new TokenResponse(token, usuario.getLogin(), usuario.getPerfil());
    }
}
