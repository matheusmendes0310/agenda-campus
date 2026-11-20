import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Participante } from '../../../core/models/participante.model';
import { AuthService } from '../../../core/services/auth.service';
import { ParticipanteService } from '../../../core/services/participante.service';
import { mensagemDeErro } from '../../../core/utils/erro-http';

@Component({
  selector: 'app-participante-lista',
  imports: [RouterLink],
  templateUrl: './participante-lista.html',
})
export class ParticipanteLista implements OnInit {
  private participanteService = inject(ParticipanteService);
  auth = inject(AuthService);

  participantes = signal<Participante[]>([]);
  carregando = signal(true);
  erro = signal('');

  ngOnInit(): void {
    this.participanteService.listar().subscribe({
      next: (lista) => {
        this.participantes.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }
}
