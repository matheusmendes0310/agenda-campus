import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Evento } from '../../../core/models/evento.model';
import { AuthService } from '../../../core/services/auth.service';
import { EventoService } from '../../../core/services/evento.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

// Tela da consulta com múltiplos parâmetros: GET /api/eventos?nome=...&data=...
@Component({
  selector: 'app-evento-lista',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './evento-lista.html',
})
export class EventoLista implements OnInit {
  private fb = inject(FormBuilder);
  private eventoService = inject(EventoService);
  auth = inject(AuthService);

  eventos = signal<Evento[]>([]);
  carregando = signal(true);
  erro = signal('');
  filtrando = signal(false);

  filtros = this.fb.nonNullable.group({
    nome: [''],
    data: [''],
  });

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    const { nome, data } = this.filtros.getRawValue();
    this.filtrando.set(!!nome.trim() || !!data);
    this.carregando.set(true);
    this.erro.set('');

    this.eventoService.listar({ nome, data }).subscribe({
      next: (lista) => {
        this.eventos.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  limpar(): void {
    this.filtros.reset();
    this.buscar();
  }

  lotado(evento: Evento): boolean {
    return evento.inscritos >= evento.vagas;
  }
}
