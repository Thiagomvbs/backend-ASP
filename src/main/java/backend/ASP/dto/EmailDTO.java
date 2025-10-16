package backend.ASP.dto;

import java.util.List;

public record EmailDTO(
        String nome,
        String email,
        String endereco,
        String bairro,
        String telefone,
        String cpfCnpj,
        String enderecoEntrega,
        String bairroEntrega,
        String dataEvento,
        String infoExtras,
        List<ProdutoDTO> produtos
) {
    public record ProdutoDTO(
            String nome,
            Integer quantidade
    ) {}
}

