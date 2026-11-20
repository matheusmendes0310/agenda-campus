import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Participante, ParticipanteRequest } from '../models/participante.model';

@Injectable({ providedIn: 'root' })
export class ParticipanteService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/participantes`;

  listar(): Observable<Participante[]> {
    return this.http.get<Participante[]>(this.url);
  }

  criar(dados: ParticipanteRequest): Observable<Participante> {
    return this.http.post<Participante>(this.url, dados);
  }
}
