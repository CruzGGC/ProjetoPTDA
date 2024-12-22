package com.grupo6.projetoptda.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoSelecionadoTest {
    private ProdutoSelecionado produtoSelecionado;
    private static final int TEST_ID = 1;
    private static final String TEST_DESCRICAO = "Test Description";
    private static final double TEST_PRECO = 29.99;
    private static final int TEST_QUANTIDADE = 5;
    private static final double TEST_TOTAL = 149.95;

    @BeforeEach
    void setUp() {
        produtoSelecionado = new ProdutoSelecionado(
                TEST_ID,
                TEST_DESCRICAO,
                TEST_PRECO,
                TEST_QUANTIDADE,
                TEST_TOTAL
        );
    }

    @Test
    void testGetIdProduto() {
        assertEquals(TEST_ID, produtoSelecionado.getIdProduto());
    }

    @Test
    void testGetPreco() {
        assertEquals(TEST_PRECO, produtoSelecionado.getPreco());
    }

    @Test
    void testGetQuantidadeStock() {
        assertEquals(TEST_QUANTIDADE, produtoSelecionado.getQuantidadeStock());
    }

    @Test
    void testGetTotal() {
        assertEquals(TEST_TOTAL, produtoSelecionado.getTotal());
    }

    @Test
    void testDescricaoProperty() {
        assertEquals(TEST_DESCRICAO, produtoSelecionado.descricaoProperty().get());
    }

    @Test
    void testPrecoProperty() {
        assertEquals(TEST_PRECO, produtoSelecionado.precoProperty().get());
    }

    @Test
    void testQuantidadeStockProperty() {
        assertEquals(TEST_QUANTIDADE, produtoSelecionado.quantidadeStockProperty().get());
    }

    @Test
    void testTotalProperty() {
        assertEquals(TEST_TOTAL, produtoSelecionado.totalProperty().get());
    }
}