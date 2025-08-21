package backend.ASP.dto;

import backend.ASP.entity.Produto;

import java.math.BigDecimal;

public record ProdutoListagemDTO(
        Long id,
        String nome,
        String descricao,
        String categoria,
        BigDecimal preco) {

    public ProdutoListagemDTO(Produto produto){
        this(produto.getId(), produto.getNome(), produto.getDescricao(),
                String.valueOf(produto.getCategoria()), produto.getPreco());
    }
}
