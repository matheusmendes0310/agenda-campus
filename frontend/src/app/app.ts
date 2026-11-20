import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { MensagemService } from './core/services/mensagem.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  auth = inject(AuthService);
  mensagens = inject(MensagemService);
  private router = inject(Router);

  sair(): void {
    this.auth.logout();
    this.mensagens.sucesso('Você saiu do sistema.');
    this.router.navigate(['/login']);
  }
}
