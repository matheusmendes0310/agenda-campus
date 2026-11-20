export type Perfil = 'ADMIN' | 'COMUM';

export interface LoginRequest {
  login: string;
  senha: string;
}

export interface TokenResponse {
  token: string;
  login: string;
  perfil: Perfil;
}

export interface UsuarioLogado {
  login: string;
  perfil: Perfil;
}
