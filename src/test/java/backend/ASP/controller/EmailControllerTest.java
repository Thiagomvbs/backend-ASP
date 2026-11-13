package backend.ASP.controller;

import backend.ASP.config.ratelimit.RateLimiter;
import backend.ASP.dto.EmailDTO;
import backend.ASP.exception.GlobalExceptionHandler;
import backend.ASP.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailControllerTest {

    private static final String JSON_VALIDO = """
            {"nome":"Maria","email":"maria@exemplo.com","telefone":"(21) 99999-9999",
             "enderecoEntrega":"Rua A, 10","bairroEntrega":"Rocha","dataEvento":"20/12/2026",
             "produtos":[{"nome":"Prato raso","quantidade":12}]}
            """;

    private EmailService emailService;
    private RateLimiter rateLimiter;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        rateLimiter = mock(RateLimiter.class);
        when(rateLimiter.permitir(anyString())).thenReturn(true);
        mvc = MockMvcBuilders.standaloneSetup(new EmailController(emailService, rateLimiter))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ResultActions enviar(String json) throws Exception {
        return mvc.perform(post("/email/enviar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    @Test
    void envioValidoResponde200EmJson() throws Exception {
        enviar(JSON_VALIDO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Orçamento enviado com sucesso!"));

        verify(emailService).enviarEmail(any(EmailDTO.class));
    }

    @Test
    void emailInvalidoResponde400SemEnviar() throws Exception {
        enviar(JSON_VALIDO.replace("maria@exemplo.com", "isto-nao-e-email"))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).enviarEmail(any(EmailDTO.class));
    }

    @Test
    void semNomeResponde400() throws Exception {
        enviar(JSON_VALIDO.replace("\"nome\":\"Maria\",", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quantidadeZeroResponde400() throws Exception {
        enviar(JSON_VALIDO.replace("\"quantidade\":12", "\"quantidade\":0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void limiteExcedidoResponde429SemEnviar() throws Exception {
        when(rateLimiter.permitir(anyString())).thenReturn(false);

        enviar(JSON_VALIDO).andExpect(status().isTooManyRequests());

        verify(emailService, never()).enviarEmail(any(EmailDTO.class));
    }

    @Test
    void falhaNoEnvioResponde503SemExporDetalhe() throws Exception {
        doThrow(new MailSendException("falha-interna-secreta")).when(emailService).enviarEmail(any(EmailDTO.class));

        enviar(JSON_VALIDO)
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(not(containsString("falha-interna-secreta"))));
    }
}