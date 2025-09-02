package backend.ASP.dto;

import backend.ASP.entity.products.Produto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long id,
        String nome,
        String descricao,
        String categoria,
        @NotNull
        BigDecimal preco
) {
    public ProdutoDTO(Produto produto){
        this(produto.getId(), produto.getNome(), produto.getDescricao(),
                String.valueOf(produto.getCategoria()), produto.getPreco());
    }
}
