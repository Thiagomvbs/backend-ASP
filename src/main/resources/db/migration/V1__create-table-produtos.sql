CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    preco NUMERIC(19, 2),
    categoria VARCHAR(50) NOT NULL
);