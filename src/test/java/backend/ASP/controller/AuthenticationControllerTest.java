package backend.ASP.controller;

import backend.ASP.config.ratelimit.RateLimiter;
import backend.ASP.config.security.TokenService;
import backend.ASP.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest {

    private static final String JSON_LOGIN = "{\"login\":\"Admin\",\"password\":\"senha-errada\"}";

    private AuthenticationManager authenticationManager;
    private RateLimiter rateLimiter;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        rateLimiter = mock(RateLimiter.class);

        AuthenticationController controller = new AuthenticationController();
        ReflectionTestUtils.setField(controller, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(controller, "tokenService", mock(TokenService.class));
        ReflectionTestUtils.setField(controller, "rateLimiter", rateLimiter);

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ResultActions login() throws Exception {
        return mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_LOGIN));
    }

    @Test
    void senhaErradaResponde401EContaComoTentativaFalha() throws Exception {
        when(rateLimiter.excedeu(anyString())).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("ruim"));

        login().andExpect(status().isUnauthorized());

        // O nome é normalizado (minúsculas) para que "Admin" e "admin" compartilhem o mesmo limite.
        verify(rateLimiter).registrar("login:admin");
    }

    @Test
    void depoisDoLimiteResponde429SemTentarAutenticar() throws Exception {
        when(rateLimiter.excedeu("login:admin")).thenReturn(true);

        login().andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(authenticationManager, never()).authenticate(any());
        verify(rateLimiter, never()).registrar(anyString());
    }
}
