import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MensagemService } from '../services/mensagem.service';

// Telas de cadastro e edição só para o perfil ADMIN
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);

  if (auth.isAdmin()) {
    return true;
  }
  inject(MensagemService).erro('Somente administradores podem acessar essa tela.');
  return inject(Router).createUrlTree(['/inscricoes']);
};
