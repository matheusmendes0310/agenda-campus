import { Injectable, signal } from '@angular/core';

export interface Mensagem {
  texto: string;
  tipo: 'sucesso' | 'erro';
}

// Mensagens de feedback exibidas no topo da tela (sucesso some sozinha depois de alguns segundos)
@Injectable({ providedIn: 'root' })
export class MensagemService {
  readonly mensagem = signal<Mensagem | null>(null);
  private timer?: ReturnType<typeof setTimeout>;

  sucesso(texto: string): void {
    this.mostrar({ texto, tipo: 'sucesso' }, 5000);
  }

  erro(texto: string): void {
    this.mostrar({ texto, tipo: 'erro' }, 8000);
  }

  fechar(): void {
    clearTimeout(this.timer);
    this.mensagem.set(null);
  }

  private mostrar(mensagem: Mensagem, duracao: number): void {
    clearTimeout(this.timer);
    this.mensagem.set(mensagem);
    this.timer = setTimeout(() => this.mensagem.set(null), duracao);
  }
}
