package backend.ASP.service;

import backend.ASP.dto.EmailDTO;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private JavaMailSender sender;
    private MimeMessage mensagem;
    private EmailService service;

    @BeforeEach
    void setUp() {
        sender = mock(JavaMailSender.class);
        mensagem = new MimeMessage((Session) null);
        when(sender.createMimeMessage()).thenReturn(mensagem);
        service = new EmailService(sender, "envio@empresa.com", "orcamentos@empresa.com");
    }

    private EmailDTO pedido(String nome) {
        return new EmailDTO(nome, "maria@exemplo.com", null, null, "(21) 99999-9999", null,
                "Rua A, 10", "Rocha", "20/12/2026", null,
                List.of(new EmailDTO.ProdutoDTO("Prato raso", 12)));
    }

    @Test
    void enviaParaAEmpresaComRespostaParaOCliente() throws Exception {
        service.enviarEmail(pedido("Maria"));

        verify(sender).send(mensagem);
        assertEquals("orcamentos@empresa.com", mensagem.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("envio@empresa.com", mensagem.getFrom()[0].toString());
        assertEquals("maria@exemplo.com", mensagem.getReplyTo()[0].toString());
    }

    @Test
    void corpoTemOsDadosEOmiteCamposVazios() {
        String corpo = service.montarCorpo(pedido("Maria"));

        assertTrue(corpo.contains("Nome: Maria"));
        assertTrue(corpo.contains("Prato raso (quantidade: 12)"));
        assertFalse(corpo.contains("null"));
        assertFalse(corpo.contains("CPF/CNPJ"));
    }

    @Test
    void quebrasDeLinhaNosCamposNaoCriamLinhasFalsas() {
        String corpo = service.montarCorpo(pedido("Maria\nTotal: R$ 0"));

        assertTrue(corpo.contains("Nome: Maria Total: R$ 0"));
        assertFalse(corpo.contains("\nTotal:"));
    }
}