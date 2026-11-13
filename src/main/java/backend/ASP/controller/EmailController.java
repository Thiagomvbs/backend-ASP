package backend.ASP.controller;

import backend.ASP.config.ratelimit.RateLimiter;
import backend.ASP.dto.EmailDTO;
import backend.ASP.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    public EmailController(EmailService emailService, RateLimiter rateLimiter) {
        this.emailService = emailService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarEmail(@Valid @RequestBody EmailDTO request,
                                                           HttpServletRequest http) {
        if (!rateLimiter.permitir(chaveDoCliente(http))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("mensagem",
                            "Muitos pedidos em pouco tempo. Tente de novo em alguns minutos ou ligue para a empresa."));
        }
        try {
            emailService.enviarEmail(request);
            return ResponseEntity.ok(Map.of("mensagem", "Orçamento enviado com sucesso!"));
        } catch (MessagingException | MailException e) {
            // O detalhe fica só no log do servidor.
            log.error("Falha ao enviar o e-mail de orçamento", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("mensagem",
                            "Não foi possível enviar o orçamento agora. Tente de novo ou fale com a empresa por telefone."));
        }
    }

    // Atrás do proxy do Render, o último valor de X-Forwarded-For é o que o proxy mais próximo anotou.
    private String chaveDoCliente(HttpServletRequest http) {
        String encaminhado = http.getHeader("X-Forwarded-For");
        if (encaminhado != null && !encaminhado.isBlank()) {
            String[] ips = encaminhado.split(",");
            return ips[ips.length - 1].trim();
        }
        return http.getRemoteAddr();
    }
}