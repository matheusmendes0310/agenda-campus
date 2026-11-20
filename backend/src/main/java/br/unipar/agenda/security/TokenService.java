package br.unipar.agenda.security;

import br.unipar.agenda.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Gera e valida o token JWT usado nas requisições protegidas
@Service
public class TokenService {

    private static final String EMISSOR = "agenda-campus-api";

    private final Algorithm assinatura;
    private final long horasValidade;

    public TokenService(@Value("${api.security.token.secret}") String segredo,
                        @Value("${api.security.token.horas-validade}") long horasValidade) {
        this.horasValidade = horasValidade;
        if (segredo.isBlank()) {
            byte[] chave = new byte[32];
            new SecureRandom().nextBytes(chave);
            this.assinatura = Algorithm.HMAC256(chave);
        } else {
            this.assinatura = Algorithm.HMAC256(segredo);
        }
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getLogin())
                .withClaim("perfil", usuario.getPerfil().name())
                .withExpiresAt(Instant.now().plus(horasValidade, ChronoUnit.HOURS))
                .sign(assinatura);
    }

    // Devolve o login do usuário, ou null se o token for inválido ou estiver expirado
    public String validarToken(String token) {
        try {
            return JWT.require(assinatura)
                    .withIssuer(EMISSOR)
                    .withClaimPresence("exp")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException erro) {
            return null;
        }
    }
}
