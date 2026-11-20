import { Component, input, output } from '@angular/core';

// Janela de confirmação reutilizada antes de excluir um registro
@Component({
  selector: 'app-confirmacao',
  templateUrl: './confirmacao.html',
})
export class Confirmacao {
  titulo = input('Confirmar');
  mensagem = input('');
  textoConfirmar = input('Confirmar');
  carregando = input(false);

  confirmar = output<void>();
  cancelar = output<void>();
}
