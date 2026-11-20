export interface Inscricao {
  id: number;
  dataInscricao: string;
  eventoId: number;
  eventoNome: string;
  eventoData: string;
  eventoLocal: string;
  participanteId: number;
  participanteNome: string;
  participanteEmail: string;
}

export interface InscricaoRequest {
  eventoId: number;
  participanteId: number;
}
