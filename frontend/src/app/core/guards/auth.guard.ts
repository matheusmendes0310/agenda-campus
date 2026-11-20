import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Impede abrir as telas internas sem login e manda para a tela de login
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.estaLogado()) {
    return true;
  }
  return router.createUrlTree(['/login'], { queryParams: { voltarPara: state.url } });
};
