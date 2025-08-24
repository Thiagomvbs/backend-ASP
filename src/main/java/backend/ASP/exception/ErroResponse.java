package backend.ASP.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        String path,
        LocalDateTime timestamp
) {
}
