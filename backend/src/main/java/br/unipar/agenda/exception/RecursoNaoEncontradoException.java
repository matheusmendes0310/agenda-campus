package br.unipar.agenda.exception;

// Lançada quando um registro não existe no banco (vira HTTP 404)
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
