package br.unipar.agenda.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(LocalDateTime dataHora, int status, String mensagem, List<CampoErro> campos) {

    public record CampoErro(String campo, String mensagem) {
    }

    public ErroResponse(int status, String mensagem) {
        this(LocalDateTime.now(), status, mensagem, List.of());
    }

    public ErroResponse(int status, String mensagem, List<CampoErro> campos) {
        this(LocalDateTime.now(), status, mensagem, campos);
    }
}
