import { Component, inject, input, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Evento } from '../../../core/models/evento.model';
import { InscricaoRequest } from '../../../core/models/inscricao.model';
import { Participante } from '../../../core/models/participante.model';
import { EventoService } from '../../../core/services/evento.service';
import { InscricaoService } from '../../../core/services/inscricao.service';
import { MensagemService } from '../../../core/services/mensagem.service';
import { ParticipanteService } from '../../../core/services/participante.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

// Mesmo formulário para cadastrar (/inscricoes/nova) e editar (/inscricoes/:id/editar)
@Component({
  selector: 'app-inscricao-form',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './inscricao-form.html',
})
export class InscricaoForm implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private inscricaoService = inject(InscricaoService);
  private eventoService = inject(EventoService);
  private participanteService = inject(ParticipanteService);
  private mensagens = inject(MensagemService);

  id = input<string>(); // existe só na edição
  eventoId = input<string>(); // opcional: ?eventoId=1 já deixa o evento selecionado

  eventos = signal<Evento[]>([]);
  participantes = signal<Participante[]>([]);
  carregando = signal(true);
  salvando = signal(false);
  erroCarregamento = signal('');
  erro = signal('');
  private eventoOriginalId: number | null = null;

  form = this.fb.group({
    eventoId: this.fb.control<number | null>(null, Validators.required),
    participanteId: this.fb.control<number | null>(null, Validators.required),
  });

  get editando(): boolean {
    return !!this.id();
  }

  ngOnInit(): void {
    // Os selects são carregados da API (relacionamento 1:N)
    forkJoin({
      eventos: this.eventoService.listar(),
      participantes: this.participanteService.listar(),
    }).subscribe({
      next: ({ eventos, participantes }) => {
        this.eventos.set(eventos);
        this.participantes.set(participantes);
        if (this.editando) {
          this.carregarInscricao();
        } else {
          const evento = eventos.find((item) => item.id === Number(this.eventoId()));
          if (evento && !this.eventoIndisponivel(evento)) {
            this.form.controls.eventoId.setValue(evento.id);
          } else if (evento) {
            this.erro.set('Não há mais vagas para este evento. Escolha outro.');
          }
          this.carregando.set(false);
        }
      },
      error: (e) => {
        this.erroCarregamento.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  private carregarInscricao(): void {
    this.inscricaoService.buscarPorId(Number(this.id())).subscribe({
      next: (inscricao) => {
        this.eventoOriginalId = inscricao.eventoId;
        this.form.setValue({ eventoId: inscricao.eventoId, participanteId: inscricao.participanteId });
        this.carregando.set(false);
      },
      error: (e) => {
        this.erroCarregamento.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  salvar(): void {
    if (this.salvando()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.erro.set('');

    const dados = this.form.getRawValue() as InscricaoRequest;
    const requisicao = this.editando
      ? this.inscricaoService.atualizar(Number(this.id()), dados)
      : this.inscricaoService.criar(dados);

    requisicao.subscribe({
      next: (salva) => {
        this.mensagens.sucesso(this.editando ? 'Inscrição atualizada com sucesso.' : 'Inscrição cadastrada com sucesso.');
        this.router.navigate(['/inscricoes', salva.id]);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.salvando.set(false);
      },
    });
  }

  lotado(evento: Evento): boolean {
    return evento.inscritos >= evento.vagas;
  }

  eventoIndisponivel(evento: Evento): boolean {
    return this.lotado(evento) && evento.id !== this.eventoOriginalId;
  }

  campoInvalido(campo: 'eventoId' | 'participanteId'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && controle.touched;
  }
}
