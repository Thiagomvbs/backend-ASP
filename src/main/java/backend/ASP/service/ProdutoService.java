package backend.ASP.service;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.dto.ProdutoDTO;
import backend.ASP.dto.ProdutoListagemDTO;
import backend.ASP.entity.Produto;
import backend.ASP.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {
    @Autowired
    private final ProdutoRepository repository;
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public ProdutoDTO criarProduto(ProdutoCadastroDTO request){
        Produto produto = new Produto(request
        );
        Produto salvo = repository.save(produto);
        return new ProdutoDTO(salvo);
    }

    public List<ProdutoListagemDTO> listarTodos(){
        return repository.findAll()
                .stream()
                .map(ProdutoListagemDTO::new)
                .collect(Collectors.toList());
    }



    public ProdutoDTO buscarPorId(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return new ProdutoDTO(p);
    }

    public ProdutoDTO atualizarProduto(Long id, ProdutoAtualizacaoDTO dto){
        Produto produto = repository.getReferenceById(id);
        produto.atualizarInformacoes(dto);
        return new ProdutoDTO(produto);
    }

    public void deletarProduto(Long id){
        Produto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        repository.delete(p);
    }

}
