package backend.ASP.controller;

import backend.ASP.dto.EmailDTO;
import backend.ASP.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarEmail(@RequestBody EmailDTO request) {
        try {
            emailService.enviarEmail(request);
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
