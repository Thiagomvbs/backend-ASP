package backend.ASP.repository;

import backend.ASP.entity.products.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Produto> findByCategoriaIn(List<String> categoria, Pageable pageable);
}

