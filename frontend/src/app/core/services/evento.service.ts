import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Evento, EventoRequest, FiltroEvento } from '../models/evento.model';
import { Participante } from '../models/participante.model';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/eventos`;

  // GET /api/eventos?nome=...&data=...  (consulta com múltiplos parâmetros)
  listar(filtro: FiltroEvento = {}): Observable<Evento[]> {
    let params = new HttpParams();
    if (filtro.nome?.trim()) params = params.set('nome', filtro.nome.trim());
    if (filtro.data) params = params.set('data', filtro.data);
    return this.http.get<Evento[]>(this.url, { params });
  }

  buscarPorId(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.url}/${id}`);
  }

  // GET /api/eventos/{id}/participantes  (consulta com filtro no path)
  listarParticipantes(id: number): Observable<Participante[]> {
    return this.http.get<Participante[]>(`${this.url}/${id}/participantes`);
  }

  criar(dados: EventoRequest): Observable<Evento> {
    return this.http.post<Evento>(this.url, dados);
  }
}
