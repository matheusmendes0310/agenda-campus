import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { MensagemService } from '../services/mensagem.service';
import { environment } from '../../../environments/environment';

// Envia o token JWT em todas as requisições e trata o 401 (token inválido ou expirado)
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(`${environment.apiUrl}/`) || req.url === `${environment.apiUrl}/auth/login`) {
    return next(req);
  }

  const auth = inject(AuthService);
  const router = inject(Router);
  const mensagens = inject(MensagemService);
  const token = auth.getToken();

  const requisicao = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(requisicao).pipe(
    catchError((erro: HttpErrorResponse) => {
      if (erro.status === 401 && auth.getToken() === token) {
        auth.logout();
        mensagens.erro('Sua sessão expirou. Faça login novamente.');
        router.navigate(['/login']);
      }
      return throwError(() => erro);
    }),
  );
};
