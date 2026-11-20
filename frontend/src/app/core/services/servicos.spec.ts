import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../environments/environment';
import { authInterceptor } from '../interceptors/auth.interceptor';
import { AuthService } from './auth.service';
import { EventoService } from './evento.service';
import { InscricaoService } from './inscricao.service';

describe('Serviços de acesso à API', () => {
  let http: HttpTestingController;
  let auth: AuthService;
  const api = environment.apiUrl;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => http.verify());

  it('guarda o token no login e envia no cabeçalho Authorization', () => {
    // token falso com "exp" no futuro
    const payload = btoa(JSON.stringify({ sub: 'admin', exp: Math.floor(Date.now() / 1000) + 3600 }));
    const token = `cabecalho.${payload}.assinatura`;

    auth.login({ login: 'admin', senha: 'admin123' }).subscribe();
    http.expectOne(`${api}/auth/login`).flush({ token, login: 'admin', perfil: 'ADMIN' });

    expect(auth.estaLogado()).toBe(true);
    expect(auth.isAdmin()).toBe(true);

    TestBed.inject(InscricaoService).listar().subscribe();
    const req = http.expectOne(`${api}/inscricoes`);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    req.flush([]);

    auth.logout();
    expect(auth.estaLogado()).toBe(false);
  });

  it('envia o filtro no path e os dois parâmetros da busca', () => {
    const eventos = TestBed.inject(EventoService);

    eventos.listar({ nome: 'Oficina', data: '2026-12-03' }).subscribe();
    http.expectOne(`${api}/eventos?nome=Oficina&data=2026-12-03`).flush([]);

    eventos.listarParticipantes(7).subscribe();
    http.expectOne(`${api}/eventos/7/participantes`).flush([]);
  });

  it('usa POST, PUT e DELETE no CRUD de inscrições', () => {
    const inscricoes = TestBed.inject(InscricaoService);
    const dados = { eventoId: 1, participanteId: 2 };

    inscricoes.criar(dados).subscribe();
    expect(http.expectOne(`${api}/inscricoes`).request.method).toBe('POST');

    inscricoes.atualizar(10, dados).subscribe();
    expect(http.expectOne(`${api}/inscricoes/10`).request.method).toBe('PUT');

    inscricoes.excluir(10).subscribe();
    expect(http.expectOne(`${api}/inscricoes/10`).request.method).toBe('DELETE');
  });
});
