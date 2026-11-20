package br.unipar.agenda.config;

import br.unipar.agenda.model.*;
import br.unipar.agenda.repository.EventoRepository;
import br.unipar.agenda.repository.InscricaoRepository;
import br.unipar.agenda.repository.ParticipanteRepository;
import br.unipar.agenda.repository.UsuarioRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Cria os usuários de acesso e alguns dados de exemplo na primeira vez que a API sobe
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final InscricaoRepository inscricaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.senha-admin}")
    private String senhaAdmin;

    @Value("${app.senha-comum}")
    private String senhaComum;

    @Value("${app.dados-exemplo}")
    private boolean criarDadosExemplo;

    public DadosIniciais(UsuarioRepository usuarioRepository, EventoRepository eventoRepository,
                         ParticipanteRepository participanteRepository, InscricaoRepository inscricaoRepository,
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarUsuario("admin", senhaAdmin, Perfil.ADMIN);
        criarUsuario("comum", senhaComum, Perfil.COMUM);

        if (criarDadosExemplo && eventoRepository.count() == 0 && participanteRepository.count() == 0) {
            LocalDate hoje = LocalDate.now();
            Evento angular = eventoRepository.save(new Evento("Oficina de Angular", hoje.plusDays(15), "Laboratório 02", 30));
            Evento spring = eventoRepository.save(new Evento("Oficina de Spring Boot", hoje.plusDays(16), "Laboratório 03", 25));
            eventoRepository.save(new Evento("Semana Acadêmica de ADS", hoje.plusDays(20), "Auditório", 100));
            Evento hackathon = eventoRepository.save(new Evento("Hackathon UNIPAR", hoje.plusDays(30), "Bloco B", 2));

            Participante ana = participanteRepository.save(new Participante("Ana Souza", "ana.souza@email.com"));
            Participante bruno = participanteRepository.save(new Participante("Bruno Lima", "bruno.lima@email.com"));
            Participante carla = participanteRepository.save(new Participante("Carla Mendes", "carla.mendes@email.com"));
            participanteRepository.save(new Participante("Diego Rocha", "diego.rocha@email.com"));

            inscricaoRepository.save(new Inscricao(angular, ana));
            inscricaoRepository.save(new Inscricao(angular, bruno));
            inscricaoRepository.save(new Inscricao(spring, carla));
            inscricaoRepository.save(new Inscricao(hackathon, ana));
            inscricaoRepository.save(new Inscricao(hackathon, carla));
        }
    }

    private void criarUsuario(String login, String senha, Perfil perfil) {
        if (!usuarioRepository.existsByLogin(login)) {
            usuarioRepository.save(new Usuario(login, passwordEncoder.encode(senha), perfil));
        }
    }
}
