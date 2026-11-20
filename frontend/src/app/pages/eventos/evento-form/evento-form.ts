import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EventoService } from '../../../core/services/evento.service';
import { MensagemService } from '../../../core/services/mensagem.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

@Component({
  selector: 'app-evento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './evento-form.html',
})
export class EventoForm {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private eventoService = inject(EventoService);
  private mensagens = inject(MensagemService);

  hoje = new Date().toLocaleDateString('sv-SE'); // formato aaaa-mm-dd para o atributo min
  salvando = signal(false);
  erro = signal('');

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    data: ['', Validators.required],
    local: ['', [Validators.required, Validators.maxLength(120)]],
    vagas: [30, [Validators.required, Validators.min(1)]],
  });

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.erro.set('');

    this.eventoService.criar(this.form.getRawValue()).subscribe({
      next: (evento) => {
        this.mensagens.sucesso(`Evento "${evento.nome}" cadastrado com sucesso.`);
        this.router.navigate(['/eventos']);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.salvando.set(false);
      },
    });
  }

  campoInvalido(campo: 'nome' | 'data' | 'local' | 'vagas'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && controle.touched;
  }
}
