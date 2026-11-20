import { HttpErrorResponse } from '@angular/common/http';

// Converte o erro devolvido pela API em uma mensagem clara para o usuário
export function mensagemDeErro(erro: unknown): string {
  if (!(erro instanceof HttpErrorResponse)) {
    return 'Ocorreu um erro inesperado.';
  }

  const mensagemApi: string | undefined = erro.error?.mensagem;

  switch (erro.status) {
    case 0:
      return 'Não foi possível conectar à API. Verifique se o back-end está rodando.';
    case 400:
      return mensagemApi ?? 'Dados inválidos. Revise o formulário.';
    case 401:
      return mensagemApi ?? 'Sessão expirada. Faça login novamente.';
    case 403:
      return 'Seu perfil não tem permissão para esta operação.';
    case 404:
      return mensagemApi ?? 'Registro não encontrado.';
    default:
      return 'Erro no servidor. Tente novamente em instantes.';
  }
}
