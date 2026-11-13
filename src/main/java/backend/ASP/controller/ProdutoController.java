package backend.ASP.controller;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.entity.products.Categoria;
import backend.ASP.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoCadastroDTO request) {
        return ResponseEntity.ok(service.criarProduto(request));
    }

    // Ainda não implementado: o armazenamento de imagens será decidido com a hospedagem.
    @PostMapping("/{id}/imagem")
    public ResponseEntity<Void> uploadImagem(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    

    @GetMapping({"/", ""})
    public ResponseEntity<Page<ProdutoListagemDTO>> listarTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<String> categorias
    ) {
        int pageIndex = (page > 0) ? page - 1 : 0;

        int tamanho = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pageIndex, tamanho, Sort.by("id").ascending());

        return ResponseEntity.ok(service.listarTodos(nome, categorias, pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoAtualizacaoDTO dto) {
        return ResponseEntity.ok(service.atualizarProduto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = Arrays.stream(Categoria.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }
}
