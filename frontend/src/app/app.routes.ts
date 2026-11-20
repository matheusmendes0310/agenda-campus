import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { Login } from './pages/login/login';
import { InscricaoLista } from './pages/inscricoes/inscricao-lista/inscricao-lista';
import { InscricaoDetalhe } from './pages/inscricoes/inscricao-detalhe/inscricao-detalhe';
import { InscricaoForm } from './pages/inscricoes/inscricao-form/inscricao-form';
import { EventoLista } from './pages/eventos/evento-lista/evento-lista';
import { EventoForm } from './pages/eventos/evento-form/evento-form';
import { EventoParticipantes } from './pages/eventos/evento-participantes/evento-participantes';
import { ParticipanteLista } from './pages/participantes/participante-lista/participante-lista';
import { ParticipanteForm } from './pages/participantes/participante-form/participante-form';

export const routes: Routes = [
  { path: 'login', component: Login, title: 'Entrar | Agenda Campus' },

  // CRUD de inscrições
  { path: 'inscricoes', component: InscricaoLista, canActivate: [authGuard], title: 'Inscrições | Agenda Campus' },
  { path: 'inscricoes/nova', component: InscricaoForm, canActivate: [authGuard, adminGuard], title: 'Nova inscrição | Agenda Campus' },
  { path: 'inscricoes/:id', component: InscricaoDetalhe, canActivate: [authGuard], title: 'Inscrição | Agenda Campus' },
  { path: 'inscricoes/:id/editar', component: InscricaoForm, canActivate: [authGuard, adminGuard], title: 'Editar inscrição | Agenda Campus' },

  // Eventos: busca com filtros (nome + data) e participantes de um evento (filtro no path)
  { path: 'eventos', component: EventoLista, canActivate: [authGuard], title: 'Eventos | Agenda Campus' },
  { path: 'eventos/novo', component: EventoForm, canActivate: [authGuard, adminGuard], title: 'Novo evento | Agenda Campus' },
  { path: 'eventos/:id/participantes', component: EventoParticipantes, canActivate: [authGuard], title: 'Participantes do evento | Agenda Campus' },

  // Participantes
  { path: 'participantes', component: ParticipanteLista, canActivate: [authGuard], title: 'Participantes | Agenda Campus' },
  { path: 'participantes/novo', component: ParticipanteForm, canActivate: [authGuard, adminGuard], title: 'Novo participante | Agenda Campus' },

  { path: '', pathMatch: 'full', redirectTo: 'inscricoes' },
  { path: '**', redirectTo: 'inscricoes' },
];
