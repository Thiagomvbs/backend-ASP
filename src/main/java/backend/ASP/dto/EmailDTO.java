package backend.ASP.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EmailDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        String email,

        @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
        String endereco,

        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
        String bairro,

        @NotBlank(message = "O telefone é obrigatório")
        @Size(max = 30, message = "O telefone deve ter no máximo 30 caracteres")
        String telefone,

        @Size(max = 20, message = "O CPF/CNPJ deve ter no máximo 20 caracteres")
        String cpfCnpj,

        @NotBlank(message = "O endereço de entrega é obrigatório")
        @Size(max = 200, message = "O endereço de entrega deve ter no máximo 200 caracteres")
        String enderecoEntrega,

        @NotBlank(message = "O bairro de entrega é obrigatório")
        @Size(max = 100, message = "O bairro de entrega deve ter no máximo 100 caracteres")
        String bairroEntrega,

        @NotBlank(message = "A data do evento é obrigatória")
        @Size(max = 20, message = "A data do evento deve ter no máximo 20 caracteres")
        String dataEvento,

        @Size(max = 2000, message = "As informações extras devem ter no máximo 2000 caracteres")
        String infoExtras,

        @Size(max = 100, message = "O orçamento pode ter no máximo 100 itens")
        List<@Valid ProdutoDTO> produtos
) {
    public record ProdutoDTO(
            @NotBlank(message = "O nome do produto é obrigatório")
            @Size(max = 200, message = "O nome do produto deve ter no máximo 200 caracteres")
            String nome,

            @NotNull(message = "A quantidade é obrigatória")
            @Min(value = 1, message = "A quantidade deve ser de pelo menos 1")
            @Max(value = 10000, message = "A quantidade é alta demais")
            Integer quantidade
    ) {}
}