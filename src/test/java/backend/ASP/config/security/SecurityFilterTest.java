package backend.ASP.config.security;

import backend.ASP.entity.user.User;
import backend.ASP.entity.user.UserRole;
import backend.ASP.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityFilterTest {

    private TokenService tokenService;
    private UserRepository userRepository;
    private SecurityFilter filter;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste-com-tamanho-suficiente");
        userRepository = mock(UserRepository.class);
        filter = new SecurityFilter();
        filter.tokenService = tokenService;
        filter.userRepository = userRepository;
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletResponse run(String authorization) throws Exception {
        var request = new MockHttpServletRequest("GET", "/produtos");
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();
        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
        assertNotNull(chain.getRequest(), "a requisição deve continuar na cadeia de filtros");
        return response;
    }

    @Test
    void tokenInvalidoNaoDerrubaARequisicaoNemAutentica() throws Exception {
        run("Bearer isto-nao-e-um-jwt");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void semCabecalhoSegueSemAutenticar() throws Exception {
        run(null);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void cabecalhoSemBearerEIgnorado() throws Exception {
        run("Basic abc123");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValidoDeUsuarioInexistenteNaoAutentica() throws Exception {
        var user = new User("admin", "hash", UserRole.ADMIN);
        String token = tokenService.generateToken(user);
        when(userRepository.findByLogin("admin")).thenReturn(null);

        run("Bearer " + token);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValidoAutenticaOUsuario() throws Exception {
        var user = new User("admin", "hash", UserRole.ADMIN);
        String token = tokenService.generateToken(user);
        when(userRepository.findByLogin("admin")).thenReturn(user);

        run("Bearer " + token);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(user, auth.getPrincipal());
    }

    @Test
    void validateTokenDevolveVazioParaTokenInvalido() {
        assertEquals("", tokenService.validateToken("lixo"));
    }
}