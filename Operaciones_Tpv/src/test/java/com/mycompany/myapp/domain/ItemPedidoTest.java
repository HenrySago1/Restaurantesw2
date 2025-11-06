package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ItemPedidoTestSamples.*;
import static com.mycompany.myapp.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemPedido.class);
        ItemPedido itemPedido1 = getItemPedidoSample1();
        ItemPedido itemPedido2 = new ItemPedido();
        assertThat(itemPedido1).isNotEqualTo(itemPedido2);

        itemPedido2.setId(itemPedido1.getId());
        assertThat(itemPedido1).isEqualTo(itemPedido2);

        itemPedido2 = getItemPedidoSample2();
        assertThat(itemPedido1).isNotEqualTo(itemPedido2);
    }

    @Test
    void pedidoTest() {
        ItemPedido itemPedido = getItemPedidoRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        itemPedido.setPedido(pedidoBack);
        assertThat(itemPedido.getPedido()).isEqualTo(pedidoBack);

        itemPedido.pedido(null);
        assertThat(itemPedido.getPedido()).isNull();
    }
}
