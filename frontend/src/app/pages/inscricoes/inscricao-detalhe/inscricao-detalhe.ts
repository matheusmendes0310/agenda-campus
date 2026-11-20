import { Component, inject, input, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Inscricao } from '../../../core/models/inscricao.model';
import { AuthService } from '../../../core/services/auth.service';
import { InscricaoService } from '../../../core/services/inscricao.service';
import { MensagemService } from '../../../core/services/mensagem.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';
import { Confirmacao } from '../../../shared/confirmacao/confirmacao';

@Component({
  selector: 'app-inscricao-detalhe',
  imports: [RouterLink, DatePipe, Confirmacao],
  templateUrl: './inscricao-detalhe.html',
})
export class InscricaoDetalhe implements OnInit {
  private inscricaoService = inject(InscricaoService);
  private mensagens = inject(MensagemService);
  private router = inject(Router);
  auth = inject(AuthService);

  id = input.required<string>(); // vem da rota /inscricoes/:id

  inscricao = signal<Inscricao | null>(null);
  erro = signal('');
  confirmando = signal(false);
  excluindo = signal(false);

  ngOnInit(): void {
    this.inscricaoService.buscarPorId(Number(this.id())).subscribe({
      next: (dados) => this.inscricao.set(dados),
      error: (e) => this.erro.set(mensagemDeErro(e)),
    });
  }

  excluir(): void {
    this.excluindo.set(true);
    this.inscricaoService.excluir(Number(this.id())).subscribe({
      next: () => {
        this.mensagens.sucesso('Inscrição excluída com sucesso.');
        this.router.navigate(['/inscricoes']);
      },
      error: (e) => {
        this.mensagens.erro(mensagemDeErro(e));
        this.confirmando.set(false);
        this.excluindo.set(false);
      },
    });
  }
}
