-- Criar a base de dados
USE PTDA24_BD_06;

-- Tabela Categoria
CREATE TABLE Categoria (
	idCategoria INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_categoria_nome (nome)
);

-- Tabela Produto
CREATE TABLE Produto (
    idProduto INT AUTO_INCREMENT PRIMARY KEY,
    idCategoria INT,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
    quantidadeStock INT NOT NULL CHECK (quantidadeStock >= 0),
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria) ON DELETE SET NULL,
    INDEX idx_produto_categoria (idCategoria),
    INDEX idx_produto_nome (nome)
);

-- Tabela Cliente
CREATE TABLE Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    INDEX idx_cliente_nome (nome)
);

-- Tabela Funcionario
CREATE TABLE Funcionario (
    idFuncionario INT AUTO_INCREMENT PRIMARY KEY,
    fNome VARCHAR(255) NOT NULL UNIQUE,
    fPassword VARCHAR(255) NOT NULL, -- Aumentado para suportar hashing mais robusto
    nivelAcesso ENUM('EmpregadoMesa', 'Gerente') NOT NULL,
    servico BOOLEAN DEFAULT FALSE,
    INDEX idx_funcionario_nome (fNome)	
);

-- Tabela Pedido
CREATE TABLE Pedido (
    idPedido INT AUTO_INCREMENT PRIMARY KEY,
    dataHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Entregue', 'PorPagar', 'Finalizado') NOT NULL DEFAULT 'Entregue',
    metodoPagamento ENUM('Multibanco', 'DinheiroVivo') NULL,
    idCliente INT,
    idFuncionario INT,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE SET NULL,
    FOREIGN KEY (idFuncionario) REFERENCES Funcionario(idFuncionario) ON DELETE SET NULL,
    INDEX idx_pedido_cliente (idCliente),
    INDEX idx_pedido_status (status)
);

-- Tabela PedidoProduto para relação muitos para muitos
CREATE TABLE PedidoProduto (
    idPedido INT,
    idProduto INT,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    PRIMARY KEY (idPedido, idProduto),
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE,
    FOREIGN KEY (idProduto) REFERENCES Produto(idProduto) ON DELETE CASCADE,
    INDEX idx_pedidoproduto_pedido (idPedido),
    INDEX idx_pedidoproduto_produto (idProduto)
);

-- Tabela Fatura
CREATE TABLE Fatura (
    idFatura INT AUTO_INCREMENT PRIMARY KEY,
    idPedido INT NOT NULL,
    idCliente INT NOT NULL,
    valorTotal DECIMAL(10,2) NOT NULL CHECK (valorTotal >= 0),
    data DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE CASCADE,
    INDEX idx_fatura_pedido (idPedido),
    INDEX idx_fatura_cliente (idCliente),
    INDEX idx_fatura_data (data)
);

-- Tabela Pagamento
CREATE TABLE IF NOT EXISTS Pagamento (
    idPagamento INT AUTO_INCREMENT PRIMARY KEY,
    idFatura INT NOT NULL,
    metodoPagamento ENUM('Multibanco', 'DinheiroVivo') NULL,
    estadoPagamento TINYINT NOT NULL,
    INDEX idx_metodoPagamento (metodoPagamento),
    FOREIGN KEY (idFatura) REFERENCES Fatura(idFatura)
);

-- Tabela Compra
CREATE TABLE Compra (
    idCompra INT AUTO_INCREMENT PRIMARY KEY,
    quantidade INT NOT NULL,
    preco DECIMAL(10,2) NOT NULL
);

-- Tabela CompraProduto
CREATE TABLE CompraProduto (
    idCompra INT,
    idProduto INT,
    quantidade INT NOT NULL,
    preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
    PRIMARY KEY (idCompra, idProduto),
    FOREIGN KEY (idCompra) REFERENCES Compra(idCompra),
    FOREIGN KEY (idProduto) REFERENCES Produto(idProduto)
);

-- Tabela FaturaCompra
CREATE TABLE FaturaCompra (
  idFatura INT AUTO_INCREMENT PRIMARY KEY,
  idCompra INT NOT NULL,
  idCliente INT NOT NULL,
  valorTotal DECIMAL(10,2) NOT NULL CHECK (valorTotal >= 0),
  data DATE NOT NULL,
  hora TIME NOT NULL,
  FOREIGN KEY (idCompra) REFERENCES Compra(idCompra) ON DELETE CASCADE,
  FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE CASCADE,
  INDEX idx_faturacompra_compra (idCompra),
  INDEX idx_faturacompra_cliente (idCliente),
  INDEX idx_faturacompra_data (data)
);

-- Tabela Turno
CREATE TABLE Turno (
    idTurno INT AUTO_INCREMENT PRIMARY KEY,
    dataHoraAbertura TIMESTAMP NOT NULL,
    dataHoraFechamento TIMESTAMP,
    idFuncionario INT,
    FOREIGN KEY (idFuncionario) REFERENCES Funcionario(idFuncionario)
);
-- Trigger para atualizar stock após finalização de pedido
DELIMITER $$

CREATE TRIGGER trg_atualizar_stock_apos_pedido 
AFTER INSERT ON PedidoProduto
FOR EACH ROW
BEGIN
    UPDATE Produto 
    SET quantidadeStock = quantidadeStock - NEW.quantidade
    WHERE idProduto = NEW.idProduto;
END$$

DELIMITER ;

-- Trigger para prevenir stock negativo
DELIMITER $$

CREATE TRIGGER trg_prevenir_stock_negativo 
BEFORE UPDATE ON Produto
FOR EACH ROW
BEGIN
    IF NEW.quantidadeStock < 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Não é possível ter stock negativo';
    END IF;
END$$

DELIMITER ;
