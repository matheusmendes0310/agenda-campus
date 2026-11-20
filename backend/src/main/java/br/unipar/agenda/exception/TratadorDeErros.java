package br.unipar.agenda.exception;

import br.unipar.agenda.dto.ErroResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Transforma as exceções em respostas JSON com status HTTP e mensagem clara
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException erro) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(404, erro.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException erro) {
        return ResponseEntity.badRequest().body(new ErroResponse(400, erro.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException erro) {
        List<ErroResponse.CampoErro> campos = erro.getBindingResult().getFieldErrors().stream()
                .map(campo -> new ErroResponse.CampoErro(campo.getField(), campo.getDefaultMessage()))
                .toList();
        String mensagem = campos.isEmpty() ? "Dados inválidos." : campos.get(0).mensagem();
        return ResponseEntity.badRequest().body(new ErroResponse(400, mensagem, campos));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResponse> formatoInvalido() {
        return ResponseEntity.badRequest()
                .body(new ErroResponse(400, "Formato inválido. Confira os campos e a data (aaaa-mm-dd)."));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> loginInvalido() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "Usuário ou senha incorretos."));
    }
}
