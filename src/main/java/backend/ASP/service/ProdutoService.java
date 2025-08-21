package backend.ASP.service;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.entity.Produto;
import backend.ASP.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {
    @Autowired
    private final ProdutoRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ProdutoService.class);
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public ProdutoDTO criarProduto(ProdutoCadastroDTO request){
        Produto produto = new Produto(request
        );
        Produto salvo = repository.save(produto);
        return new ProdutoDTO(salvo);
    }

    public Page<ProdutoListagemDTO> listarTodos(Pageable pageable){
        return repository.findAll(pageable)
                .map(ProdutoListagemDTO::new);
    }

    public ProdutoDTO buscarPorId(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return new ProdutoDTO(p);
    }

    public ProdutoDTO atualizarProduto(Long id, ProdutoAtualizacaoDTO dto){
        log.info("Atualizando produto id={}", id);
        Produto produto = repository.getReferenceById(id);
        produto.atualizarInformacoes(dto);
        Produto atualizado = repository.save(produto);
        log.info("Produto atualizado: {}", atualizado.getNome());
        return new ProdutoDTO(atualizado);
    }

    public void deletarProduto(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        repository.delete(p);
    }

}
