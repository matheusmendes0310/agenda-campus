import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, TokenResponse, UsuarioLogado } from '../models/usuario.model';

const CHAVE_TOKEN = 'agenda_token';
const CHAVE_USUARIO = 'agenda_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  // Usuário logado guardado em um signal para o menu e as telas reagirem ao login/logout
  private usuarioAtual = signal<UsuarioLogado | null>(this.lerUsuarioSalvo());
  readonly usuario = this.usuarioAtual.asReadonly();
  readonly isAdmin = computed(() => this.usuarioAtual()?.perfil === 'ADMIN');

  login(dados: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${environment.apiUrl}/auth/login`, dados).pipe(
      tap((resposta) => {
        localStorage.setItem(CHAVE_TOKEN, resposta.token);
        const usuario: UsuarioLogado = { login: resposta.login, perfil: resposta.perfil };
        localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario));
        this.usuarioAtual.set(usuario);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(CHAVE_TOKEN);
    localStorage.removeItem(CHAVE_USUARIO);
    this.usuarioAtual.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(CHAVE_TOKEN);
  }

  estaLogado(): boolean {
    const token = this.getToken();
    if (!token || this.tokenExpirado(token)) {
      if (token) this.logout();
      return false;
    }
    return this.usuarioAtual() !== null;
  }

  // O JWT tem o campo "exp" (segundos). Se já passou, o usuário precisa entrar de novo.
  private tokenExpirado(token: string): boolean {
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(atob(base64));
      return typeof payload.exp !== 'number' || payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private lerUsuarioSalvo(): UsuarioLogado | null {
    try {
      const salvo = localStorage.getItem(CHAVE_USUARIO);
      return salvo ? (JSON.parse(salvo) as UsuarioLogado) : null;
    } catch {
      return null;
    }
  }
}
