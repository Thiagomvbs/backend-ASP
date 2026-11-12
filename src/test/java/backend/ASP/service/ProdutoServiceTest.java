package backend.ASP.service;

import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.entity.products.Produto;
import backend.ASP.exception.RequisicaoInvalidaException;
import backend.ASP.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProdutoServiceTest {

    private ProdutoRepository repository;
    private ProdutoService service;
    private final Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        repository = mock(ProdutoRepository.class);
        service = new ProdutoService(repository);
    }

    private Produto umProduto() {
        return new Produto(new ProdutoCadastroDTO("Prato raso", "PRATOS", null, "Prato de porcelana", "prato.webp"));
    }

    @Test
    void cadastroCopiaOCampoImagem() {
        assertEquals("prato.webp", umProduto().getImagem());
    }

    @Test
    void listagemSemFiltrosDevolveOsProdutos() {
        when(repository.findAll(ArgumentMatchers.<Specification<Produto>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(umProduto()), pageable, 1));

        Page<ProdutoListagemDTO> resultado = service.listarTodos(null, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Prato raso", resultado.getContent().get(0).nome());
    }

    @Test
    void categoriaInvalidaViraRequisicaoInvalidaSemConsultarOBanco() {
        assertThrows(RequisicaoInvalidaException.class,
                () -> service.listarTodos(null, List.of("GELADEIRAS"), pageable));

        verify(repository, never()).findAll(ArgumentMatchers.<Specification<Produto>>any(), any(Pageable.class));
    }

    @Test
    void categoriasEmBrancoSaoIgnoradas() {
        when(repository.findAll(ArgumentMatchers.<Specification<Produto>>any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<ProdutoListagemDTO> resultado = service.listarTodos("", List.of("", " "), pageable);

        assertEquals(0, resultado.getTotalElements());
        verify(repository).findAll(ArgumentMatchers.<Specification<Produto>>any(), eq(pageable));
    }
}