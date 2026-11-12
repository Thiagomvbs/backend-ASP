package backend.ASP.exception;

import backend.ASP.entity.products.Categoria;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    @Controller
    @RequestMapping("/teste")
    class TesteController {
        @GetMapping("/nao-encontrado")
        @ResponseBody
        String naoEncontrado() {
            throw new EntityNotFoundException("Produto não encontrado");
        }

        @GetMapping("/credenciais")
        @ResponseBody
        String credenciais() {
            throw new BadCredentialsException("senha errada");
        }

        @GetMapping("/invalido")
        @ResponseBody
        String invalido() {
            throw new RequisicaoInvalidaException("Categoria inválida: X");
        }

        @GetMapping("/explode")
        @ResponseBody
        String explode() {
            throw new IllegalStateException("detalhe-interno-secreto");
        }

        @GetMapping("/numero/{id}")
        @ResponseBody
        String numero(@PathVariable Long id) {
            return String.valueOf(id);
        }
    }

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TesteController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void entidadeInexistenteViraNotFound() throws Exception {
        mvc.perform(get("/teste/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Produto não encontrado"));
    }

    @Test
    void credenciaisErradasViramUnauthorizedSemVazarDetalhe() throws Exception {
        mvc.perform(get("/teste/credenciais"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Usuário ou senha inválidos"));
    }

    @Test
    void requisicaoInvalidaViraBadRequest() throws Exception {
        mvc.perform(get("/teste/invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Categoria inválida: X"));
    }

    @Test
    void erroInesperadoViraInternalServerErrorSemExporDetalhe() throws Exception {
        mvc.perform(get("/teste/explode"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString("detalhe-interno-secreto"))));
    }

    @Test
    void idNaoNumericoViraBadRequest() throws Exception {
        mvc.perform(get("/teste/numero/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void metodoNaoPermitidoViraMethodNotAllowedEmVezDe500() throws Exception {
        mvc.perform(post("/teste/nao-encontrado"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void categoriaFromStringAceitaMinusculasEEspacos() {
        assertEquals(Categoria.PRATOS, Categoria.fromString(" pratos "));
    }

    @Test
    void categoriaFromStringRejeitaValorInvalido() {
        assertThrows(RequisicaoInvalidaException.class, () -> Categoria.fromString("GELADEIRAS"));
        assertThrows(RequisicaoInvalidaException.class, () -> Categoria.fromString(" "));
        assertThrows(RequisicaoInvalidaException.class, () -> Categoria.fromString(null));
    }
}