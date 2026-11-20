export interface Evento {
  id: number;
  nome: string;
  data: string; // aaaa-mm-dd
  local: string;
  vagas: number;
  inscritos: number;
}

export interface EventoRequest {
  nome: string;
  data: string;
  local: string;
  vagas: number;
}

export interface FiltroEvento {
  nome?: string;
  data?: string;
}
