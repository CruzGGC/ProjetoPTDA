package com.grupo6.projetoptda.Getter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    private static final int TEST_ID = 1;
    private static final String TEST_NOME = "João Silva";

    @Test
    void testClienteConstructorAndGetters() {
        Cliente cliente = new Cliente(TEST_ID, TEST_NOME);

        assertEquals(TEST_ID, cliente.idCliente());
        assertEquals(TEST_NOME, cliente.nome());
    }

    @Test
    void testToString() {
        Cliente cliente = new Cliente(TEST_ID, TEST_NOME);
        assertEquals(TEST_NOME, cliente.toString());
    }

    @Test
    void testEquality() {
        Cliente cliente1 = new Cliente(TEST_ID, TEST_NOME);
        Cliente cliente2 = new Cliente(TEST_ID, TEST_NOME);
        Cliente cliente3 = new Cliente(2, "Maria Santos");

        assertEquals(cliente1, cliente2);
        assertNotEquals(cliente1, cliente3);
    }
}