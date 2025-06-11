CREATE TABLE Estacionamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    capacidade INT NOT NULL
);

CREATE TABLE Contratante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefone VARCHAR(20)
);

CREATE TABLE Veiculo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) UNIQUE NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    cor VARCHAR(50)
);

CREATE TABLE Evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_evento VARCHAR(100) NOT NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    descricao TEXT
);

CREATE TABLE Tempo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    duracao TIME,
    valor_fracao DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(5,2)
);

CREATE TABLE Diaria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(10,2) NOT NULL,
    tipo VARCHAR(50),
    descricao VARCHAR(255)
);

CREATE TABLE Diaria_Noturna (
    id INT PRIMARY KEY,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    adicional_noturno DECIMAL(10,2),
    CONSTRAINT fk_diaria_noturna_diaria
        FOREIGN KEY (id) REFERENCES Diaria(id)
);

CREATE TABLE Mensalista (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(10,2) NOT NULL,
    periodo_meses INT,
    descricao VARCHAR(255)
);

CREATE TABLE Acesso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estacionamento_id INT NOT NULL,
    veiculo_id INT NOT NULL,
    tempo_id INT,
    entrada DATETIME NOT NULL,
    saida DATETIME,
    valor_cobrado DECIMAL(10,2),
    tipo_acesso VARCHAR(50) NOT NULL,
    diaria_id INT,
    mensalista_id INT,
    CONSTRAINT fk_acesso_estacionamento
        FOREIGN KEY (estacionamento_id) REFERENCES Estacionamento(id),
    CONSTRAINT fk_acesso_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES Veiculo(id),
    CONSTRAINT fk_acesso_tempo
        FOREIGN KEY (tempo_id) REFERENCES Tempo(id),
    CONSTRAINT fk_acesso_diaria
        FOREIGN KEY (diaria_id) REFERENCES Diaria(id),
    CONSTRAINT fk_acesso_mensalista
        FOREIGN KEY (mensalista_id) REFERENCES Mensalista(id)
);

CREATE TABLE Estacionamento_Contratante (
    estacionamento_id INT NOT NULL,
    contratante_id INT NOT NULL,
    PRIMARY KEY (estacionamento_id, contratante_id),
    CONSTRAINT fk_ec_estacionamento
        FOREIGN KEY (estacionamento_id) REFERENCES Estacionamento(id),
    CONSTRAINT fk_ec_contratante
        FOREIGN KEY (contratante_id) REFERENCES Contratante(id)
);

CREATE TABLE Contratante_Evento (
    contratante_id INT NOT NULL
    evento_id INT NOT NULL,
    PRIMARY KEY (contratante_id, evento_id),
    CONSTRAINT fk_ce_contratante
        FOREIGN KEY (contratante_id) REFERENCES Contratante(id),
    CONSTRAINT fk_ce_evento
        FOREIGN KEY (evento_id) REFERENCES Evento(id)
);