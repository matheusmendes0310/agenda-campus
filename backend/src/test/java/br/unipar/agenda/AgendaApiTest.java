package br.unipar.agenda;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.unipar.agenda.model.Perfil;
import br.unipar.agenda.model.Usuario;
import br.unipar.agenda.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

// Testa a API inteira (controller + service + repository) usando banco H2 em memória
@SpringBootTest
@AutoConfigureMockMvc
class AgendaApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PasswordEncoder senhas;

    @Test
    void tokenDeUsuarioApagadoRetorna401() throws Exception {
        Usuario usuario = usuarios.save(new Usuario("temporario", senhas.encode("senha-teste"), Perfil.COMUM));
        String token = login("temporario", "senha-teste");
        usuarios.deleteById(usuario.getId());

        mvc.perform(get("/api/inscricoes").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void swaggerPublicaContratoComEsquemaBearer() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes['bearer-jwt'].scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/inscricoes/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/api/eventos/{id}/participantes'].get").exists());
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mvc.perform(get("/api/inscricoes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Usuário ou senha incorretos."));
    }

    @Test
    void adminFazCrudCompletoEConsultasComFiltro() throws Exception {
        String admin = login("admin", "admin123");
        String data = LocalDate.now().plusDays(10).toString();

        long eventoId = idDe(mvc.perform(json(post("/api/eventos"), admin,
                        "{\"nome\":\"Oficina de Testes\",\"data\":\"" + data + "\",\"local\":\"Lab 1\",\"vagas\":10}"))
                .andExpect(status().isCreated()));
        long outroEventoId = idDe(mvc.perform(json(post("/api/eventos"), admin,
                        "{\"nome\":\"Palestra de Java\",\"data\":\"" + data + "\",\"local\":\"Auditório\",\"vagas\":5}"))
                .andExpect(status().isCreated()));
        long participanteId = idDe(mvc.perform(json(post("/api/participantes"), admin,
                        "{\"nome\":\"Maria Teste\",\"email\":\"maria.teste@email.com\"}"))
                .andExpect(status().isCreated()));

        // CREATE
        long inscricaoId = idDe(mvc.perform(json(post("/api/inscricoes"), admin,
                        "{\"eventoId\":" + eventoId + ",\"participanteId\":" + participanteId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventoNome").value("Oficina de Testes")));

        // Regra de negócio: não pode repetir a inscrição
        mvc.perform(json(post("/api/inscricoes"), admin,
                        "{\"eventoId\":" + eventoId + ",\"participanteId\":" + participanteId + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Este participante já está inscrito neste evento."));

        // READ (lista e por ID)
        mvc.perform(get("/api/inscricoes").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mvc.perform(get("/api/inscricoes/" + inscricaoId).header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participanteNome").value("Maria Teste"));

        // Filtro no path
        mvc.perform(get("/api/eventos/" + eventoId + "/participantes").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Maria Teste"));

        // Filtro com múltiplos parâmetros
        mvc.perform(get("/api/eventos").param("nome", "oficina").param("data", data)
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Oficina de Testes"));

        // UPDATE
        mvc.perform(json(put("/api/inscricoes/" + inscricaoId), admin,
                        "{\"eventoId\":" + outroEventoId + ",\"participanteId\":" + participanteId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventoNome").value("Palestra de Java"));

        // DELETE
        mvc.perform(delete("/api/inscricoes/" + inscricaoId).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/inscricoes/" + inscricaoId).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNotFound());
    }

    @Test
    void validacaoRetorna400ComMensagem() throws Exception {
        String admin = login("admin", "admin123");
        mvc.perform(json(post("/api/inscricoes"), admin, "{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos").isArray());
        mvc.perform(json(post("/api/eventos"), admin,
                        "{\"nome\":\"Antigo\",\"data\":\"2020-01-01\",\"local\":\"Lab\",\"vagas\":5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A data do evento não pode estar no passado."));
    }

    @Test
    void perfilComumSoConsulta() throws Exception {
        String comum = login("comum", "comum123");
        mvc.perform(get("/api/eventos").header("Authorization", "Bearer " + comum))
                .andExpect(status().isOk());
        mvc.perform(json(post("/api/inscricoes"), comum, "{\"eventoId\":1,\"participanteId\":1}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/inscricoes/1").header("Authorization", "Bearer " + comum))
                .andExpect(status().isForbidden());
    }

    @Test
    void eventoLotadoAceitaSomenteEdicaoQueNaoOcupaOutraVaga() throws Exception {
        String admin = login("admin", "admin123");
        String data = LocalDate.now().plusDays(12).toString();
        long eventoId = idDe(mvc.perform(json(post("/api/eventos"), admin,
                "{\"nome\":\"Evento de Uma Vaga\",\"data\":\"" + data + "\",\"local\":\"Lab\",\"vagas\":1}"))
                .andExpect(status().isCreated()));
        long primeira = criarPessoa(admin, "primeira@example.org");
        long segunda = criarPessoa(admin, "segunda@example.org");
        long inscricaoId = idDe(mvc.perform(json(post("/api/inscricoes"), admin, dadosInscricao(eventoId, primeira)))
                .andExpect(status().isCreated()));

        mvc.perform(json(post("/api/inscricoes"), admin, dadosInscricao(eventoId, segunda)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Não há mais vagas para este evento."));

        mvc.perform(json(put("/api/inscricoes/" + inscricaoId), admin, dadosInscricao(eventoId, segunda)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participanteId").value(segunda));
    }

    @Test
    void duasInscricoesAoMesmoTempoNaoUltrapassamAsVagas() throws Exception {
        String admin = login("admin", "admin123");
        String data = LocalDate.now().plusDays(12).toString();
        long evento = idDe(mvc.perform(json(post("/api/eventos"), admin,
                "{\"nome\":\"Teste simultâneo\",\"data\":\"" + data + "\",\"local\":\"Lab\",\"vagas\":1}"))
                .andExpect(status().isCreated()));
        long primeira = criarPessoa(admin, "simultaneo1@example.org");
        long segunda = criarPessoa(admin, "simultaneo2@example.org");
        var executor = Executors.newFixedThreadPool(2);
        var inicio = new CountDownLatch(1);
        try {
            var primeiraResposta = executor.submit(() -> enviarInscricao(inicio, admin, evento, primeira));
            var segundaResposta = executor.submit(() -> enviarInscricao(inicio, admin, evento, segunda));
            inicio.countDown();
            var status = List.of(primeiraResposta.get(10, TimeUnit.SECONDS), segundaResposta.get(10, TimeUnit.SECONDS));
            assertEquals(List.of(201, 400), status.stream().sorted().toList());
        } finally {
            executor.shutdownNow();
        }
    }

    private int enviarInscricao(CountDownLatch inicio, String admin, long evento, long participante) throws Exception {
        inicio.await();
        return mvc.perform(json(post("/api/inscricoes"), admin, dadosInscricao(evento, participante)))
                .andReturn().getResponse().getStatus();
    }

    private long criarPessoa(String token, String email) throws Exception {
        return idDe(mvc.perform(json(post("/api/participantes"), token,
                "{\"nome\":\"Pessoa Teste\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isCreated()));
    }

    private String dadosInscricao(long eventoId, long pessoaId) {
        return "{\"eventoId\":" + eventoId + ",\"participanteId\":" + pessoaId + "}";
    }

    private String login(String usuario, String senha) throws Exception {
        String resposta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + usuario + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder req, String token, String corpo) {
        return req.header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo);
    }

    private long idDe(org.springframework.test.web.servlet.ResultActions resultado) throws Exception {
        String corpo = resultado.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }
}
