import { Component, inject, input, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Evento } from '../../../core/models/evento.model';
import { Participante } from '../../../core/models/participante.model';
import { AuthService } from '../../../core/services/auth.service';
import { EventoService } from '../../../core/services/evento.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

// Tela da consulta com filtro no path: GET /api/eventos/{id}/participantes
@Component({
  selector: 'app-evento-participantes',
  imports: [RouterLink, DatePipe],
  templateUrl: './evento-participantes.html',
})
export class EventoParticipantes implements OnInit {
  private eventoService = inject(EventoService);
  auth = inject(AuthService);

  id = input.required<string>(); // vem da rota /eventos/:id/participantes

  evento = signal<Evento | null>(null);
  participantes = signal<Participante[]>([]);
  erro = signal('');

  ngOnInit(): void {
    const id = Number(this.id());
    forkJoin({
      evento: this.eventoService.buscarPorId(id),
      participantes: this.eventoService.listarParticipantes(id),
    }).subscribe({
      next: ({ evento, participantes }) => {
        this.evento.set(evento);
        this.participantes.set(participantes);
      },
      error: (e) => this.erro.set(mensagemDeErro(e)),
    });
  }
}
