CREATE DATABASE IF NOT EXISTS evento_db;
USE evento_db;

CREATE TABLE IF NOT EXISTS categorias (
	id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nm_categoria VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS enderecos (
    id_endereco INT AUTO_INCREMENT PRIMARY KEY,
    logradouro VARCHAR(255) NOT NULL,
    nr_logradouro VARCHAR(255) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(255) NOT NULL,
    nr_cep VARCHAR(10) NOT NULL,
);

CREATE TABLE IF NOT EXISTS usuarios (
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nm_usuario VARCHAR(255) NOT NULL,
    endereco_id INT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    FOREIGN KEY (endereco_id) REFERENCES enderecos(id_endereco)
);

CREATE TABLE IF NOT EXISTS eventos (
	id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nm_evento VARCHAR(255) NOT NULL,
    endereco_id INT NOT NULL,
    categoria_id INT NOT NULL,
    usuario_id INT NOT NULL,
    dt_hr_evento DATETIME NOT NULL,
    ds_evento TEXT,
    qt_limite_vagas_evento INT DEFAULT 0,
    FOREIGN KEY (endereco_id) REFERENCES enderecos(id_endereco),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id_categoria),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS confirmacoes_presença (
	usuario_id INT NOT NULL,
    evento_id INT NOT NULL,
    dt_hr_confirmacao DATETIME NOT NULL,
    st_confirmacao_evento VARCHAR(50) NOT NULL, -- CONFIRMADO / CANCELADO / EM ESPERA
    UNIQUE (usuario_id, evento_id), -- Garante que o usuário só pode confirmar presença em um evento uma vez
    PRIMARY KEY (usuario_id, evento_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (evento_id) REFERENCES eventos(id_evento)
);

-- Inserir algumas categorias inicias
INSERT INTO categorias (nm_categoria) VALUES ('Show')
ON DUPLICATE KEY UPDATE nm_categoria = nm_categoria; -- Atualiza o nome para ele mesmo se já existe

INSERT INTO categorias (nm_categoria) VALUES ('Palestra')
ON DUPLICATE KEY UPDATE nm_categoria = nm_categoria;

INSERT INTO categorias (nm_categoria) VALUES ('Workshop')
ON DUPLICATE KEY UPDATE nm_categoria = nm_categoria;

INSERT INTO categorias (nm_categoria) VALUES ('Festival')
ON DUPLICATE KEY UPDATE nm_categoria = nm_categoria;