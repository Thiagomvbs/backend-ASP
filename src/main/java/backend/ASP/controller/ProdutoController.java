package backend.ASP.controller;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Page<ProdutoListagemDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
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
}
