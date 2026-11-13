package backend.ASP.service;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.entity.products.Produto;
import backend.ASP.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import backend.ASP.entity.products.Categoria;
import backend.ASP.repository.ProdutoSpecs;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Service
public class ProdutoService {
    @Autowired
    private final ProdutoRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ProdutoService.class);
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;

    }

    public ProdutoDTO criarProduto(ProdutoCadastroDTO request){
        log.info("Iniciando cadastro de produto: {}", request.nome());
        Produto produto = new Produto(request);
        Produto salvo = repository.save(produto);
        log.info("Produto cadastrado com sucesso: id={}, nome={}", salvo.getId(), salvo.getNome());
        return new ProdutoDTO(salvo);
    }

    public Page<ProdutoListagemDTO> listarTodos(String nome, List<String> categorias, Pageable pageable){
        Specification<Produto> filtro = (root, query, cb) -> cb.conjunction();

        if (nome != null && !nome.isBlank()) {
            filtro = filtro.and(ProdutoSpecs.nomeContem(nome));
        }

        List<Categoria> categoriasValidas = categorias == null ? List.of() : categorias.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(Categoria::fromString)
                .toList();
        if (!categoriasValidas.isEmpty()) {
            filtro = filtro.and(ProdutoSpecs.categoriaEm(categoriasValidas));
        }

        return repository.findAll(filtro, pageable).map(ProdutoListagemDTO::new);
    }

    public ProdutoDTO buscarPorId(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return new ProdutoDTO(p);
    }

    public ProdutoDTO atualizarProduto(Long id, ProdutoAtualizacaoDTO dto){
        log.info("Atualizando produto id={}", id);
        Produto produto = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.atualizarInformacoes(dto);
        Produto atualizado = repository.save(produto);
        log.info("Produto atualizado: id={}, nome={}", atualizado.getId(), atualizado.getNome());
        return new ProdutoDTO(atualizado);
    }

    public void deletarProduto(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        log.warn("Solicitação de exclusão recebida para produto id={}", id);
        repository.delete(p);
        log.info("Produto excluído com sucesso: id={}", id);
    }

}
