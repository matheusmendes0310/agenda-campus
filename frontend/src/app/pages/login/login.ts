import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MensagemService } from '../../core/services/mensagem.service';
import { mensagemDeErro } from '../../core/utils/erro-http';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private mensagens = inject(MensagemService);

  enviando = signal(false);
  erro = signal('');

  form = this.fb.nonNullable.group({
    login: ['', Validators.required],
    senha: ['', Validators.required],
  });

  constructor() {
    // Se já estiver logado, não precisa ver a tela de login
    if (this.auth.estaLogado()) {
      this.router.navigate(['/inscricoes']);
    }
  }

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.erro.set('');

    this.auth.login(this.form.getRawValue()).subscribe({
      next: (resposta) => {
        this.mensagens.sucesso(`Bem-vindo(a), ${resposta.login}!`);
        const destino = this.route.snapshot.queryParamMap.get('voltarPara') ?? '/inscricoes';
        this.router.navigateByUrl(destino);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.enviando.set(false);
      },
    });
  }

  campoInvalido(campo: 'login' | 'senha'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && controle.touched;
  }
}
