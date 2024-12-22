package com.grupo6.projetoptda.Getter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {
    private static final int TEST_ID = 1;
    private static final String TEST_STATUS = "Pendente";

    @Test
    void testPedidoConstructorAndGetters() {
        Pedido pedido = new Pedido(TEST_ID, TEST_STATUS);

        assertEquals(TEST_ID, pedido.idPedido());
        assertEquals(TEST_STATUS, pedido.status());
    }

    @Test
    void testEquality() {
        Pedido pedido1 = new Pedido(TEST_ID, TEST_STATUS);
        Pedido pedido2 = new Pedido(TEST_ID, TEST_STATUS);
        Pedido pedido3 = new Pedido(2, "Concluído");

        assertEquals(pedido1, pedido2);
        assertNotEquals(pedido1, pedido3);
    }

    @Test
    void testDifferentStatus() {
        Pedido pedido1 = new Pedido(TEST_ID, "Pendente");
        Pedido pedido2 = new Pedido(TEST_ID, "Concluído");

        assertNotEquals(pedido1, pedido2);
    }
}