package com.grupo6.projetoptda.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {
    private Produto produto;
    private static final int TEST_ID = 1;
    private static final int TEST_CATEGORIA_ID = 2;
    private static final String TEST_NOME = "Test Product";
    private static final double TEST_PRECO = 29.99;
    private static final int TEST_QUANTIDADE = 10;

    @BeforeEach
    void setUp() {
        produto = new Produto(TEST_ID, TEST_CATEGORIA_ID, TEST_NOME, TEST_PRECO, TEST_QUANTIDADE);
    }

    @Test
    void testGetIdProduto() {
        assertEquals(TEST_ID, produto.getIdProduto());
    }

    @Test
    void testGetIdCategoria() {
        assertEquals(TEST_CATEGORIA_ID, produto.getIdCategoria());
    }

    @Test
    void testGetNome() {
        assertEquals(TEST_NOME, produto.getNome());
    }

    @Test
    void testGetPreco() {
        assertEquals(TEST_PRECO, produto.getPreco());
    }

    @Test
    void testGetQuantidadeStock() {
        assertEquals(TEST_QUANTIDADE, produto.getQuantidadeStock());
    }

    @Test
    void testGetQuantidade() {
        assertEquals(TEST_QUANTIDADE, produto.getQuantidade());
    }
}