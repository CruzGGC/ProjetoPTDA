-- Base de Dados de POS para Café
USE PTDA24_BD_06;

-- Stored Procedure para autenticar Funcionário (Feito, parte Java usando o BCrypt)
DELIMITER $$
CREATE PROCEDURE autenticar(IN p_nome VARCHAR(255))
BEGIN
    DECLARE v_count INT;
    
    -- Validar entrada
    IF p_nome IS NULL OR p_nome = '' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Nome de utilizador inválido';
    END IF;
    
    -- Verificar existência do utilizador
    SELECT fPassword, idFuncionario, nivelAcesso 
    FROM Funcionario
    WHERE fNome = p_nome;
END$$
DELIMITER ;

-- Procedure para Visualizar Relatório com Filtros
DELIMITER $$
CREATE PROCEDURE visualizarRelatorio(
    IN p_dataInicio DATE, 
    IN p_dataFim DATE
)
BEGIN
    -- Validar datas
    IF p_dataInicio IS NULL OR p_dataFim IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Datas de início e fim são obrigatórias';
    END IF;
    
    IF p_dataInicio > p_dataFim THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Data de início deve ser anterior à data de fim';
    END IF;
    
    -- Relatório detalhado de faturas
    SELECT 
        f.idFatura,
        f.data,
        f.hora,
        c.nome AS nomeCliente,
        f.valorTotal,
        p.status AS statusPedido
    FROM Fatura f
    JOIN Cliente c ON f.idCliente = c.idCliente
    JOIN Pedido p ON f.idPedido = p.idPedido
    WHERE f.data BETWEEN p_dataInicio AND p_dataFim
    ORDER BY f.data, f.hora;
END$$
DELIMITER ;

-- Procedure para Visualizar Relatório de Compra com Filtros
DELIMITER $$
CREATE PROCEDURE visualizarRelatorioCompra(
    IN p_dataInicio DATE,
    IN p_dataFim DATE
)
BEGIN
    -- Validate dates
    IF p_dataInicio IS NULL OR p_dataFim IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Start and end dates are required';
    END IF;

    IF p_dataInicio > p_dataFim THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Start date must be before end date';
    END IF;

    -- Detailed report of purchase invoices
    SELECT
        f.idFatura,
        f.data,
        f.hora,
        c.nome AS nomeCliente,
        f.valorTotal
    FROM FaturaCompra f
    JOIN Cliente c ON f.idCliente = c.idCliente
    WHERE f.data BETWEEN p_dataInicio AND p_dataFim
    ORDER BY f.data, f.hora;
END$$
DELIMITER ;

-- Procedure para Fechar Conta (Gerente)
DELIMITER $$
CREATE PROCEDURE fecharConta()
BEGIN
    DECLARE v_total_vendas DECIMAL(10,2);
    DECLARE v_total_pedidos INT;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Calcular total de vendas do dia
    SELECT COALESCE(SUM(valorTotal), 0) INTO v_total_vendas
    FROM Fatura
    WHERE data = CURDATE();
    
    -- Contar total de pedidos finalizados no dia
    SELECT COUNT(*) INTO v_total_pedidos
    FROM Pedido
    WHERE DATE(dataHora) = CURDATE() AND status = 'Finalizado';
    
    -- Criar um resumo do dia
    SELECT 
        CURDATE() AS data_fecho,
        v_total_vendas AS total_vendas,
        v_total_pedidos AS total_pedidos;
    COMMIT;
END$$
DELIMITER ;

-- Procedure para Criar Categoria
DELIMITER $$
CREATE PROCEDURE adicionarCategoria(IN p_nome VARCHAR(100))
BEGIN
    DECLARE EXIT HANDLER FOR 1062 
    BEGIN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Categoria já existe';
    END;
    
    INSERT INTO Categoria (nome) VALUES (p_nome);
END$$
DELIMITER ;

-- Procedure para Adicionar Produto com Validações
DELIMITER $$
CREATE PROCEDURE adicionarProduto(
    IN p_nome VARCHAR(100), 
    IN p_idCategoria INT,
    IN p_preco DECIMAL(10,2), 
    IN p_quantidade INT
)
BEGIN
    DECLARE v_idProduto INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;

    -- Validações
    IF p_nome IS NULL OR p_nome = '' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Nome do produto inválido';
    END IF;
    
    IF p_preco < 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Preço não pode ser negativo';
    END IF;
    
    IF p_quantidade < 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Quantidade em stock não pode ser negativa';
    END IF;
    
    -- Iniciar transação para garantir integridade
    START TRANSACTION;
    
    -- Verificar se o produto já existe
    SELECT idProduto INTO v_idProduto
    FROM Produto
    WHERE nome = p_nome AND idCategoria = p_idCategoria;
    
    IF v_idProduto IS NOT NULL THEN
        -- Produto já existe, atualizar stock
        CALL atualizarStock(v_idProduto, p_quantidade);
    ELSE
        -- Produto não existe, inserir novo produto
        INSERT INTO Produto (nome, idCategoria, preco, quantidadeStock) 
        VALUES (p_nome, p_idCategoria, p_preco, p_quantidade);
    END IF;
    
    -- Verificar erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao adicionar ou atualizar produto';
    ELSE
        COMMIT;
    END IF;
    
END$$
DELIMITER ;

-- Procedure para Remover Produto com Validações
DELIMITER $$
CREATE PROCEDURE removerProduto(IN p_idProduto INT)
BEGIN
    DECLARE v_existe INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Verificar se o produto existe
    SELECT COUNT(*) INTO v_existe 
    FROM Produto 
    WHERE idProduto = p_idProduto;
    
    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Produto não existe';
    END IF;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Remover produtos associados a pedidos
    DELETE FROM PedidoProduto WHERE idProduto = p_idProduto;
    
    -- Remover o produto
    DELETE FROM Produto WHERE idProduto = p_idProduto;
    
    -- Verificar erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao remover produto';
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- Procedure para Modificar Produto com Validações
DELIMITER $$
CREATE PROCEDURE modificarProduto(
    IN p_idProduto INT, 
    IN p_nome VARCHAR(100), 
    IN p_preco DECIMAL(10,2), 
    IN p_quantidade INT
)
BEGIN
    DECLARE v_existe INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Validações de entrada
    IF p_nome IS NULL OR p_nome = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Nome do produto inválido';
    END IF;
    
    IF p_preco < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Preço não pode ser negativo';
    END IF;
    
    IF p_quantidade < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantidade em stock não pode ser negativa';
    END IF;
    
    -- Verificar se o produto existe
    SELECT COUNT(*) INTO v_existe 
    FROM Produto 
    WHERE idProduto = p_idProduto;
    
    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Produto não existe';
    END IF;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Atualizar produto
    UPDATE Produto
    SET 
        nome = p_nome, 
        preco = p_preco, 
        quantidadeStock = p_quantidade
    WHERE idProduto = p_idProduto;
    
    -- Verificar erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao modificar produto';
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- Procedure para Alerta de Stock Baixo com Melhorias
DELIMITER $$
CREATE PROCEDURE alertaStockBaixo(IN p_limite INT)
BEGIN
    -- Validar limite
    IF p_limite < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Limite de stock deve ser um número positivo';
    END IF;
    
    -- Selecionar produtos com stock abaixo do limite
    SELECT 
        idProduto, 
        nome, 
        quantidadeStock, 
        preco
    FROM Produto 
    WHERE quantidadeStock < p_limite
    ORDER BY quantidadeStock ASC;
END$$
DELIMITER ;

-- Procedure para Atualizar Stock com Validações
DELIMITER $$
CREATE PROCEDURE atualizarStock(IN p_idProduto INT, IN p_quantidade INT)
BEGIN
    DECLARE v_stock_atual INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Verificar se o produto existe
    SELECT quantidadeStock INTO v_stock_atual
    FROM Produto 
    WHERE idProduto = p_idProduto;
    
    IF v_stock_atual IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Produto não encontrado';
    END IF;
    
    -- Validar quantidade
    IF (v_stock_atual + p_quantidade) < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Atualização de stock resultaria em valor negativo';
    END IF;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Atualizar stock
    UPDATE Produto 
    SET quantidadeStock = quantidadeStock + p_quantidade 
    WHERE idProduto = p_idProduto;
    
    -- Verificar erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao atualizar stock';
    ELSE
        COMMIT;
    END IF;
    
    -- Retornar stock atualizado
    SELECT 
        idProduto, 
        nome, 
        quantidadeStock AS novo_stock
    FROM Produto 
    WHERE idProduto = p_idProduto;
END$$
DELIMITER ;


-- Procedure para Criar Pedido com Gestão Transacional
DELIMITER $$

CREATE PROCEDURE criarPedido(IN p_idCliente INT, IN p_produtos JSON, IN p_idFuncionario INT)
BEGIN
    DECLARE v_idPedido INT;
    DECLARE v_total_produtos INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;

    START TRANSACTION;

    IF NOT EXISTS (SELECT 1 FROM Cliente WHERE idCliente = p_idCliente) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cliente não existe';
    END IF;

    SET v_total_produtos = JSON_LENGTH(p_produtos);
    IF v_total_produtos = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pedido deve ter pelo menos um produto';
    END IF;

    INSERT INTO Pedido (idCliente, status, idFuncionario)
    VALUES (p_idCliente, 'Entregue', p_idFuncionario);

    SET v_idPedido = LAST_INSERT_ID();

    INSERT INTO PedidoProduto (idPedido, idProduto, quantidade, preco)
    SELECT
        v_idPedido,
        CAST(jt.idProduto AS UNSIGNED),
        CAST(jt.quantidade AS UNSIGNED),
        CAST(jt.preco AS DECIMAL(10,2)) -- Adiciona o preço do produto
    FROM JSON_TABLE(p_produtos, '$[*]'
                    COLUMNS (
                        idProduto VARCHAR(10) PATH '$.idProduto',
                        quantidade VARCHAR(10) PATH '$.quantidade',
                        preco VARCHAR(10) PATH '$.preco' -- Adiciona o preço do produto
                        )
         ) AS jt;

    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erro ao criar pedido';
    ELSE
        COMMIT;
    END IF;

    SELECT v_idPedido AS idPedido;
END$$

DELIMITER ;


-- Procedure para Personalizar Pedido com Validações
DELIMITER $$
CREATE PROCEDURE personalizarPedido(IN p_idPedido INT, IN p_produtos JSON, IN p_idFuncionario INT)
BEGIN
    DECLARE v_pedido_existe INT;
    DECLARE v_total_produtos INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;

    SELECT COUNT(*) INTO v_pedido_existe
    FROM Pedido
    WHERE idPedido = p_idPedido;

    IF v_pedido_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pedido não encontrado';
    END IF;

    SET v_total_produtos = JSON_LENGTH(p_produtos);
    IF v_total_produtos = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pedido deve ter pelo menos um produto';
    END IF;

    START TRANSACTION;

    DELETE FROM PedidoProduto WHERE idPedido = p_idPedido;

    INSERT INTO PedidoProduto (idPedido, idProduto, quantidade)
    SELECT
        p_idPedido,
        CAST(jt.idProduto AS UNSIGNED),
        CAST(jt.quantidade AS UNSIGNED)
    FROM JSON_TABLE(p_produtos, '$[*]'
                    COLUMNS (
                        idProduto VARCHAR(10) PATH '$.idProduto',
                        quantidade VARCHAR(10) PATH '$.quantidade'
                        )
         ) AS jt;

    UPDATE Pedido
    SET idFuncionario = p_idFuncionario
    WHERE idPedido = p_idPedido;

    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erro ao personalizar pedido';
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- Procedure para Cancelar Pedido com Tratamento Transacional
DELIMITER $$
CREATE PROCEDURE cancelarPedido(IN p_idPedido INT)
BEGIN
    DECLARE v_existe INT;
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Verificar se o pedido existe
    SELECT COUNT(*) INTO v_existe 
    FROM Pedido 
    WHERE idPedido = p_idPedido;
    
    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Pedido não encontrado';
    END IF;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Remover produtos associados ao pedido
    DELETE FROM PedidoProduto WHERE idPedido = p_idPedido;
    
    -- Remover o pedido
    DELETE FROM Pedido WHERE idPedido = p_idPedido;
    
    -- Verificar erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao cancelar pedido';
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- Procedure para Finalizar Pedido com Verificações Avançadas
DELIMITER $$
CREATE PROCEDURE finalizarPedido(IN p_idPedido INT)
BEGIN
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE v_stock_suficiente BOOLEAN DEFAULT TRUE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Verificar se o pedido existe e não está finalizado
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE idPedido = p_idPedido AND status != 'Finalizado') THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'O pedido não existe ou já foi finalizado';
    END IF;
    
    -- Verificar stock de cada produto
    IF NOT EXISTS (
        SELECT 1 
        FROM PedidoProduto pp
        JOIN Produto p ON pp.idProduto = p.idProduto
        WHERE pp.idPedido = p_idPedido 
          AND p.quantidadeStock >= pp.quantidade
    ) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Stock insuficiente para um ou mais produtos';
    END IF;
    
    -- Atualizar status do pedido para Por Pagar
    UPDATE Pedido
    SET status = 'PorPagar'
    WHERE idPedido = p_idPedido;
    
    -- Verificar se houve erros
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erro ao finalizar pedido';
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- Procedure para Fazer Pagamento
DELIMITER $$
CREATE PROCEDURE fazerPagamento(
    IN p_idPedido INT, 
    IN p_metodoPagamento ENUM('Multibanco', 'DinheiroVivo')
)
BEGIN
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE v_valorTotal DECIMAL(10,2);
    DECLARE v_idFatura INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;
    
    -- Iniciar transação
    START TRANSACTION;
    
    -- Verificar se o pedido existe e está por pagar
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE idPedido = p_idPedido AND status = 'PorPagar') THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Pagamento só pode ser realizado para pedidos por pagar';
    END IF;
    
    -- Obter o valor total do pedido
    SELECT SUM(pp.quantidade * p.preco) INTO v_valorTotal
    FROM PedidoProduto pp
    JOIN Produto p ON pp.idProduto = p.idProduto
    WHERE pp.idPedido = p_idPedido;
    
    -- Atualizar pedido com metodo de pagamento e status
    UPDATE Pedido
    SET 
        metodoPagamento = p_metodoPagamento,
        status = 'Finalizado'
    WHERE idPedido = p_idPedido;
    
    -- Verificar se houve erros até aqui
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erro ao atualizar o status do pedido';
    END IF;
    
    -- Obter o ID da fatura correspondente
    SELECT idFatura INTO v_idFatura
    FROM Fatura
    WHERE idPedido = p_idPedido;
    
    -- Verificar se a fatura existe
    IF v_idFatura IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Fatura correspondente não encontrada';
    END IF;
    
    -- Inserir registro de pagamento na tabela Pagamento
    INSERT INTO Pagamento (idFatura, metodoPagamento, estadoPagamento)
    VALUES (v_idFatura, 
            CASE 
                WHEN p_metodoPagamento = 'Multibanco' THEN 1
                WHEN p_metodoPagamento = 'DinheiroVivo' THEN 2
            END,
            1); -- Estado do pagamento como "1 - Processado com sucesso"
    
    -- Verificar se houve erros na inserção
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erro ao registrar pagamento';
    ELSE
        COMMIT;
    END IF;
    
    -- Retornar detalhes do pagamento
    SELECT 
        p_idPedido AS idPedido,
        v_idFatura AS idFatura,
        v_valorTotal AS valorTotal,
        p_metodoPagamento AS metodoPagamento,
        'Pagamento processado com sucesso' AS mensagem;
END$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE emitirFatura(IN p_idPedido INT)
BEGIN
    -- Declaração de variáveis com tipos de dados precisos
    DECLARE v_idCliente INT;
    DECLARE v_valorTotal DECIMAL(10,2);
    DECLARE v_idFatura INT;
    DECLARE v_statusPedido VARCHAR(50);
    
    -- Declaração de handler para erros de banco de dados
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        -- Rollback em caso de erro
        ROLLBACK;
        
        -- Relançar erro personalizado
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro ao processar a fatura. Verifique os dados do pedido.';
    END;
    
    -- Iniciar transação para garantir atomicidade
    START TRANSACTION;
    
    -- Verificação mais robusta do pedido
    SELECT idCliente, status INTO v_idCliente, v_statusPedido
    FROM Pedido
    WHERE idPedido = p_idPedido FOR UPDATE;
    
    -- Validações adicionais
    IF v_idCliente IS NULL THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Pedido não encontrado.';
    END IF;
    
    IF v_statusPedido != 'PorPagar' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A fatura só pode ser emitida para pedidos com status "Por Pagar".';
    END IF;
    
    -- Cálculo do valor total com tratamento para valores zero
    SELECT COALESCE(SUM(pp.quantidade * pr.preco), 0) INTO v_valorTotal
    FROM PedidoProduto pp
    JOIN Produto pr ON pp.idProduto = pr.idProduto
    WHERE pp.idPedido = p_idPedido;
    
    -- Validação de valor total
    IF v_valorTotal = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Não é possível gerar fatura para pedido com valor zero.';
    END IF;
    
    -- Inserir fatura com tratamento de erro
    INSERT INTO Fatura (idPedido, idCliente, valorTotal, data, hora)
    VALUES (p_idPedido, v_idCliente, v_valorTotal, CURDATE(), CURTIME());
    
    -- Obter o ID da fatura recém-criada
    SET v_idFatura = LAST_INSERT_ID();
    
    -- Atualizar status do pedido
    UPDATE Pedido
    SET status = 'Finalizado'
    WHERE idPedido = p_idPedido;
    
    -- Confirmar transação
    COMMIT;
END$$
DELIMITER ;

-- Procedure para Mostrar Detalhes da Fatura com Informações Detalhadas
DELIMITER $$

CREATE PROCEDURE mostrarDetalhesFatura(IN p_idFatura INT)
BEGIN
    -- Verificar se a fatura existe
    IF NOT EXISTS (SELECT 1 FROM Fatura WHERE idFatura = p_idFatura) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A fatura especificada não existe';
    END IF;

    -- Selecionar detalhes completos da fatura com produtos concatenados
    SELECT
        f.idFatura AS 'ID Fatura',
        f.data AS 'Data',
        f.hora AS 'Hora',
        c.nome AS 'Nome do Cliente',
        pd.idPedido AS 'ID Pedido',
        GROUP_CONCAT(CONCAT(pr.nome, ' (', pp.quantidade, ' x ', pp.preco, ')') SEPARATOR ', ') AS 'Produtos',
        f.valorTotal AS 'Total Fatura'
    FROM Fatura f
             LEFT JOIN Pedido pd ON f.idPedido = pd.idPedido
             LEFT JOIN Cliente c ON f.idCliente = c.idCliente
             LEFT JOIN PedidoProduto pp ON pp.idPedido = f.idPedido
             LEFT JOIN Produto pr ON pp.idProduto = pr.idProduto
    WHERE f.idFatura = p_idFatura
    GROUP BY f.idFatura, f.data, f.hora, c.nome, pd.idPedido, f.valorTotal;
END$$

DELIMITER ;

DELIMITER $$
CREATE PROCEDURE mostrarDetalhesFaturaCompra(IN p_idFatura INT)
BEGIN
    -- Verificar se a fatura existe
    IF NOT EXISTS (SELECT 1 FROM FaturaCompra WHERE idFatura = p_idFatura) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A fatura especificada não existe';
    END IF;

    -- Selecionar detalhes completos da fatura com produtos concatenados
    SELECT
        f.idFatura AS 'ID Fatura',
        f.data AS 'Data',
        f.hora AS 'Hora',
        c.nome AS 'Nome do Cliente',
        cp.idCompra AS 'ID Compra',
        GROUP_CONCAT(CONCAT(pr.nome, ' (', cpp.quantidade, ' x ', pr.preco, ')') SEPARATOR ', ') AS 'Produtos',
        f.valorTotal AS 'Total Fatura'
    FROM FaturaCompra f
             LEFT JOIN Compra cp ON f.idCompra = cp.idCompra
             LEFT JOIN Cliente c ON f.idCliente = c.idCliente
             LEFT JOIN CompraProduto cpp ON cpp.idCompra = f.idCompra
             LEFT JOIN Produto pr ON cpp.idProduto = pr.idProduto
    WHERE f.idFatura = p_idFatura
    GROUP BY f.idFatura, f.data, f.hora, c.nome, cp.idCompra, f.valorTotal;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE efetuarCompra(IN p_produtos JSON)
BEGIN
    DECLARE v_idCompra INT;
    DECLARE v_idProduto INT;
    DECLARE v_nome VARCHAR(100);
    DECLARE v_idCategoria INT;
    DECLARE v_quantidade INT;
    DECLARE v_preco DECIMAL(10,2);
    DECLARE v_erro BOOLEAN DEFAULT FALSE;
    DECLARE v_index INT DEFAULT 0;
    DECLARE v_total_produtos INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET v_erro = TRUE;

    -- Iniciar transação
    START TRANSACTION;

    -- Contar produtos na compra
    SET v_total_produtos = JSON_LENGTH(p_produtos);
    IF v_total_produtos = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Compra deve ter pelo menos um produto';
    END IF;

    -- Criar compra
    INSERT INTO Compra (quantidade, preco)
    VALUES (0, 0);  -- Valores temporários, serão atualizados depois

    SET v_idCompra = LAST_INSERT_ID();

    -- Inserir produtos na compra
    WHILE v_index < v_total_produtos DO
            SET v_nome = JSON_UNQUOTE(JSON_EXTRACT(p_produtos, CONCAT('$[', v_index, '].nome')));
            SET v_idCategoria = JSON_UNQUOTE(JSON_EXTRACT(p_produtos, CONCAT('$[', v_index, '].idCategoria')));
            SET v_quantidade = JSON_UNQUOTE(JSON_EXTRACT(p_produtos, CONCAT('$[', v_index, '].quantidade')));
            SET v_preco = JSON_UNQUOTE(JSON_EXTRACT(p_produtos, CONCAT('$[', v_index, '].preco')));

            -- Verificar se o produto já existe
            SELECT idProduto INTO v_idProduto
            FROM Produto
            WHERE nome = v_nome AND idCategoria = v_idCategoria;

            IF v_idProduto IS NULL THEN
                -- Produto não existe, inserir novo produto
                INSERT INTO Produto (nome, idCategoria, preco, quantidadeStock)
                VALUES (v_nome, v_idCategoria, v_preco, v_quantidade);
                SET v_idProduto = LAST_INSERT_ID();
            END IF;

            -- Inserir produto na compra
            INSERT INTO CompraProduto (idCompra, idProduto, quantidade, preco)
            VALUES (v_idCompra, v_idProduto, v_quantidade, v_preco);

            SET v_index = v_index + 1;
        END WHILE;

    -- Atualizar quantidade e preço total na tabela Compra
    UPDATE Compra
    SET quantidade = (SELECT SUM(quantidade) FROM CompraProduto WHERE idCompra = v_idCompra),
        preco = (SELECT SUM(quantidade * preco) FROM CompraProduto WHERE idCompra = v_idCompra)
    WHERE idCompra = v_idCompra;

    -- Verificar se houve erro
    IF v_erro THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erro ao efetuar compra';
    ELSE
        COMMIT;
    END IF;

    SELECT v_idCompra AS idCompra;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE emitirFaturaCompra(IN p_idCompra INT)
BEGIN
    DECLARE v_idCliente INT DEFAULT 2; -- Fixed idCliente
    DECLARE v_valorTotal DECIMAL(10,2);
    DECLARE v_idFatura INT;

    -- Debugging: Output the input parameter
    SELECT 'Input p_idCompra:', p_idCompra;

    -- Check if the idCompra exists in the Compra table
    IF NOT EXISTS (SELECT 1 FROM Compra WHERE idCompra = p_idCompra) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'idCompra does not exist in Compra table.';
    END IF;

    SELECT COALESCE(SUM(cp.quantidade * cp.preco), 0) INTO v_valorTotal
    FROM CompraProduto cp
    WHERE cp.idCompra = p_idCompra;

    -- Debugging: Output the total value of the compra
    SELECT 'Total Value of Compra:', v_valorTotal;

    IF v_valorTotal = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Não é possível gerar fatura para compra com valor zero.';
    END IF;

    INSERT INTO FaturaCompra (idCompra, idCliente, valorTotal, data, hora)
    VALUES (p_idCompra, v_idCliente, v_valorTotal, CURDATE(), CURTIME());

    SET v_idFatura = LAST_INSERT_ID();

    -- Debugging: Output the ID of the newly created fatura
    SELECT 'ID of Fatura:', v_idFatura;
END$$
DELIMITER ;

-- Procedure para Criar Cliente com Validações
DELIMITER $$
CREATE PROCEDURE criarCliente(IN p_nomeCliente VARCHAR(100))
BEGIN
    -- Validar nome do cliente
    IF p_nomeCliente IS NULL OR p_nomeCliente = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Nome do cliente inválido';
    END IF;
    
    -- Verificar se cliente já existe
    IF EXISTS (SELECT 1 FROM Cliente WHERE nome = p_nomeCliente) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cliente já existe';
    END IF;
    
    -- Inserir cliente
    INSERT INTO Cliente (nome) VALUES (p_nomeCliente);
    
END$$
DELIMITER ;

-- Procedure para Abrir o Turno
DELIMITER $$
CREATE PROCEDURE registrarAberturaTurno(IN funcionarioId INT)
BEGIN
    UPDATE Funcionario SET servico = TRUE WHERE idFuncionario = funcionarioId;
    INSERT INTO Turno (dataHoraAbertura, idFuncionario) VALUES (NOW(), funcionarioId);
END $$
DELIMITER ;

-- Procedure para Fechar o Turno
DELIMITER $$
CREATE PROCEDURE registrarFechamentoTurno(IN funcionarioId INT)
BEGIN
    DECLARE turnoId INT;

    SELECT MAX(idTurno) INTO turnoId
    FROM Turno
    WHERE dataHoraFechamento IS NULL AND idFuncionario = funcionarioId;

    UPDATE Turno
    SET dataHoraFechamento = NOW()
    WHERE idTurno = turnoId;

    UPDATE Funcionario SET servico = FALSE WHERE idFuncionario = funcionarioId;
END $$
DELIMITER ;