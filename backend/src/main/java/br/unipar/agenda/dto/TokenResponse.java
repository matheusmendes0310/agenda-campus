package br.unipar.agenda.dto;

import br.unipar.agenda.model.Perfil;

public record TokenResponse(String token, String login, Perfil perfil) {
}
