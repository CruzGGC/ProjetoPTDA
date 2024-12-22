package com.grupo6.projetoptda.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {
    private Categoria categoria;
    private static final int TEST_ID = 1;
    private static final String TEST_NOME = "Bebidas";

    @BeforeEach
    void setUp() {
        categoria = new Categoria(TEST_ID, TEST_NOME);
    }

    @Test
    void testGetIdCategoria() {
        assertEquals(TEST_ID, categoria.getIdCategoria());
    }

    @Test
    void testGetNome() {
        assertEquals(TEST_NOME, categoria.getNome());
    }

    @Test
    void testSetNome() {
        String novoNome = "Comidas";
        categoria.setNome(novoNome);
        assertEquals(novoNome, categoria.getNome());
    }

    @Test
    void testToString() {
        assertEquals(TEST_NOME, categoria.toString());
    }
}