package backend.ASP.repository;

import backend.ASP.entity.products.Categoria;
import backend.ASP.entity.products.Produto;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

public final class ProdutoSpecs {

    private ProdutoSpecs() {
    }

    public static Specification<Produto> nomeContem(String nome) {
        String padrao = "%" + escapar(nome.trim().toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.<String>get("nome")), padrao, '\\');
    }

    public static Specification<Produto> categoriaEm(List<Categoria> categorias) {
        return (root, query, cb) -> root.get("categoria").in(categorias);
    }

    // Se o cliente digitar % ou _ na busca, eles valem como texto comum, e não como curinga do SQL.
    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}