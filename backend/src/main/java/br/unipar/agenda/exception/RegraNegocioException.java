package br.unipar.agenda.exception;

// Lançada quando uma regra de negócio da camada de serviço é violada (vira HTTP 400)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
