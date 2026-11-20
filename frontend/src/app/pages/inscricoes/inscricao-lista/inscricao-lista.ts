import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Inscricao } from '../../../core/models/inscricao.model';
import { AuthService } from '../../../core/services/auth.service';
import { InscricaoService } from '../../../core/services/inscricao.service';
import { MensagemService } from '../../../core/services/mensagem.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';
import { Confirmacao } from '../../../shared/confirmacao/confirmacao';

@Component({
  selector: 'app-inscricao-lista',
  imports: [RouterLink, DatePipe, Confirmacao],
  templateUrl: './inscricao-lista.html',
})
export class InscricaoLista implements OnInit {
  private inscricaoService = inject(InscricaoService);
  private mensagens = inject(MensagemService);
  auth = inject(AuthService);

  inscricoes = signal<Inscricao[]>([]);
  carregando = signal(true);
  erro = signal('');
  paraExcluir = signal<Inscricao | null>(null);
  excluindo = signal(false);

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set('');
    this.inscricaoService.listar().subscribe({
      next: (lista) => {
        this.inscricoes.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  excluir(): void {
    const inscricao = this.paraExcluir();
    if (!inscricao) return;

    this.excluindo.set(true);
    this.inscricaoService.excluir(inscricao.id).subscribe({
      next: () => {
        this.mensagens.sucesso('Inscrição excluída com sucesso.');
        this.inscricoes.update((lista) => lista.filter((i) => i.id !== inscricao.id));
        this.fecharConfirmacao();
      },
      error: (e) => {
        this.mensagens.erro(mensagemDeErro(e));
        this.fecharConfirmacao();
      },
    });
  }

  fecharConfirmacao(): void {
    this.paraExcluir.set(null);
    this.excluindo.set(false);
  }
}
