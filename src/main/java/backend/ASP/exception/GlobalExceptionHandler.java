package backend.ASP.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request){
        return erro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ErroResponse> handleRequisicaoInvalida(RequisicaoInvalidaException ex, HttpServletRequest request){
        return erro(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), request);
    }

    // Só o login lança AuthenticationException dentro de um controller (credenciais erradas).
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request){
        return erro(HttpStatus.UNAUTHORIZED, "Não autorizado", "Usuário ou senha inválidos", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos");

        return erro(HttpStatus.BAD_REQUEST, "Erro de validação", mensagem, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleCorpoIlegivel(HttpMessageNotReadableException ex, HttpServletRequest request){
        return erro(HttpStatus.BAD_REQUEST, "Requisição inválida", "Corpo da requisição ausente ou malformado", request);
    }

    // Ex.: GET /produtos/abc (id que não é número).
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTipoInvalido(TypeMismatchException ex, HttpServletRequest request){
        return erro(HttpStatus.BAD_REQUEST, "Requisição inválida", "Valor inválido para o parâmetro informado", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        // Sem este desvio, erros do próprio Spring (405 método não permitido, 404 rota inexistente,
        // parâmetro obrigatório ausente...) caíam aqui e viravam HTTP 500.
        if (ex instanceof ErrorResponse frameworkError) {
            HttpStatus status = HttpStatus.resolve(frameworkError.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                String mensagem = status == HttpStatus.NOT_FOUND
                        ? "Recurso não encontrado"
                        : frameworkError.getBody().getDetail();
                return erro(status, status.getReasonPhrase(), mensagem, request);
            }
        }

        // O detalhe fica só no log do servidor; o cliente não recebe mensagens internas.
        log.error("Erro não tratado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.", request);
    }

    private ResponseEntity<ErroResponse> erro(HttpStatus status, String titulo, String mensagem, HttpServletRequest request) {
        ErroResponse corpo = new ErroResponse(
                status.value(),
                titulo,
                mensagem,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(corpo);
    }
}