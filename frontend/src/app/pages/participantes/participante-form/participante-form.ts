import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MensagemService } from '../../../core/services/mensagem.service';
import { ParticipanteService } from '../../../core/services/participante.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

@Component({
  selector: 'app-participante-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './participante-form.html',
})
export class ParticipanteForm {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private participanteService = inject(ParticipanteService);
  private mensagens = inject(MensagemService);

  salvando = signal(false);
  erro = signal('');

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
  });

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.erro.set('');

    this.participanteService.criar(this.form.getRawValue()).subscribe({
      next: (participante) => {
        this.mensagens.sucesso(`Participante "${participante.nome}" cadastrado com sucesso.`);
        this.router.navigate(['/participantes']);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.salvando.set(false);
      },
    });
  }

  campoInvalido(campo: 'nome' | 'email'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && controle.touched;
  }
}
