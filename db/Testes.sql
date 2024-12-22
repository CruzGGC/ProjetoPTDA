-- Ficheiro de Testes para Stored Procedures
USE PTDA24_BD_06;

-- 1. Teste de Adicionar Categoria
CALL adicionarCategoria('Café');
CALL adicionarCategoria('Snacks');
CALL adicionarCategoria('Vinhos');
CALL adicionarCategoria('Espirituosas');

-- 2. Teste de Adicionar Produto
CALL adicionarProduto('Espresso', 1, 0.50, 250);
CALL adicionarProduto('Espresso Chocolate', 1, 0.75, 150);
CALL adicionarProduto('Croissant', 2, 1.00, 50);
CALL adicionarProduto('Croissant com Chocolate', 2, 1.20, 65);
CALL adicionarProduto('Avareza', 3, 3.00, 15);
CALL adicionarProduto('Torre', 3, 9.00, 20);
CALL adicionarProduto('Alentejo', 3, 7.00, 30);
CALL adicionarProduto('Cartuxa', 3, 19.99, 40);
CALL adicionarProduto('Vale Dona Maria Vinha do Rio Tinto', 3, 120.99, 5);
CALL adicionarProduto('Jägermeister', 4, 19.95, 950);
CALL adicionarProduto('Eristoff Vodka Triple Destilled', 4, 13.45, 850);
CALL adicionarProduto('Sierra', 4, 12.95, 20);
CALL adicionarProduto('Spiryitus', 4, 13.95, 15);
CALL adicionarProduto('Everclear', 4, 23.95, 12);
CALL adicionarProduto('Fireball', 4, 19.95, 22);
CALL adicionarProduto('Licor Beirao', 4, 9.95, 69);
CALL adicionarProduto('Don Julio', 4, 33.95, 12);

-- 3. Teste de Criar Cliente
CALL criarCliente('Cliente Anônimo');
CALL criarCliente('DigiGest');
CALL criarCliente('João Silva');
CALL criarCliente('Maria Santos');