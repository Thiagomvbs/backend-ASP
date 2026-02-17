package backend.ASP.dto;

import backend.ASP.entity.products.Produto;

import java.math.BigDecimal;

public record ProdutoListagemDTO(
        Long id,
        String nome,
        String descricao,
        String categoria,
        BigDecimal preco,

        String imagem

) {

    public ProdutoListagemDTO(Produto produto){
        this(produto.getId(), produto.getNome(), produto.getDescricao(),
                String.valueOf(produto.getCategoria()), produto.getPreco(), produto.getImagem());
    }
}
