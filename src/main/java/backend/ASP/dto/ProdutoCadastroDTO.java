package backend.ASP.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoCadastroDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que 0.0")
        BigDecimal preco,
        @NotBlank
        String descricao
) {
}


