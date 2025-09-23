package backend.ASP.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoAtualizacaoDTO(
        @NotBlank
        String nome,
        String descricao,
        String categoria,
        @NotNull
        BigDecimal preco,
        String imagemUrl
) {
}
