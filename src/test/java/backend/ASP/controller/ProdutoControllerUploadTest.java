package backend.ASP.controller;

import backend.ASP.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutoControllerUploadTest {

    @Test
    void uploadDeImagemResponde501PoisAindaNaoExiste() throws Exception {
        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(new ProdutoController(mock(ProdutoService.class)))
                .build();

        mvc.perform(post("/produtos/1/imagem"))
                .andExpect(status().isNotImplemented());
    }
}