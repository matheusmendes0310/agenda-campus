import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Inscricao, InscricaoRequest } from '../models/inscricao.model';

// Todas as chamadas HTTP do CRUD de inscrições ficam aqui, fora dos componentes
@Injectable({ providedIn: 'root' })
export class InscricaoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/inscricoes`;

  listar(): Observable<Inscricao[]> {
    return this.http.get<Inscricao[]>(this.url);
  }

  buscarPorId(id: number): Observable<Inscricao> {
    return this.http.get<Inscricao>(`${this.url}/${id}`);
  }

  criar(dados: InscricaoRequest): Observable<Inscricao> {
    return this.http.post<Inscricao>(this.url, dados);
  }

  atualizar(id: number, dados: InscricaoRequest): Observable<Inscricao> {
    return this.http.put<Inscricao>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
