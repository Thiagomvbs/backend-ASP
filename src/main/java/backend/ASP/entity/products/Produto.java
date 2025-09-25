package backend.ASP.entity.products;

import backend.ASP.dto.ProdutoAtualizacaoDTO;
import backend.ASP.dto.ProdutoCadastroDTO;
import backend.ASP.entity.products.Categoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@AllArgsConstructor
@EqualsAndHashCode
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(length = 500)
    private String descricao;
    @Column(nullable = false)
    private BigDecimal preco;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;
    @Column(name = "imagem_url")
    private String imagemUrl;

    public Produto(){}

    public Produto(ProdutoCadastroDTO dto){
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.categoria = Categoria.valueOf(dto.categoria());
        this.preco = dto.preco();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }
    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void atualizarInformacoes(ProdutoAtualizacaoDTO dto){
        if(dto.nome() != null){
            this.nome = dto.nome();
        }
        if(dto.categoria() != null){
            this.categoria = Categoria.valueOf(dto.categoria());
        }
        if(dto.descricao() != null){
            this.descricao = dto.descricao();
        }
        if(dto.preco() != null){
            this.preco = dto.preco();
        }
        if(dto.imagemUrl() != null){
            this.imagemUrl = dto.imagemUrl();
        }

    }
}
