package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FacturaTestSamples.*;
import static com.mycompany.myapp.domain.ItemPedidoTestSamples.*;
import static com.mycompany.myapp.domain.MesaTestSamples.*;
import static com.mycompany.myapp.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pedido.class);
        Pedido pedido1 = getPedidoSample1();
        Pedido pedido2 = new Pedido();
        assertThat(pedido1).isNotEqualTo(pedido2);

        pedido2.setId(pedido1.getId());
        assertThat(pedido1).isEqualTo(pedido2);

        pedido2 = getPedidoSample2();
        assertThat(pedido1).isNotEqualTo(pedido2);
    }

    @Test
    void facturaTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        Factura facturaBack = getFacturaRandomSampleGenerator();

        pedido.setFactura(facturaBack);
        assertThat(pedido.getFactura()).isEqualTo(facturaBack);

        pedido.factura(null);
        assertThat(pedido.getFactura()).isNull();
    }

    @Test
    void itemPedidoTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        ItemPedido itemPedidoBack = getItemPedidoRandomSampleGenerator();

        pedido.addItemPedido(itemPedidoBack);
        assertThat(pedido.getItemPedidos()).containsOnly(itemPedidoBack);
        assertThat(itemPedidoBack.getPedido()).isEqualTo(pedido);

        pedido.removeItemPedido(itemPedidoBack);
        assertThat(pedido.getItemPedidos()).doesNotContain(itemPedidoBack);
        assertThat(itemPedidoBack.getPedido()).isNull();

        pedido.itemPedidos(new HashSet<>(Set.of(itemPedidoBack)));
        assertThat(pedido.getItemPedidos()).containsOnly(itemPedidoBack);
        assertThat(itemPedidoBack.getPedido()).isEqualTo(pedido);

        pedido.setItemPedidos(new HashSet<>());
        assertThat(pedido.getItemPedidos()).doesNotContain(itemPedidoBack);
        assertThat(itemPedidoBack.getPedido()).isNull();
    }

    @Test
    void mesaTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        Mesa mesaBack = getMesaRandomSampleGenerator();

        pedido.setMesa(mesaBack);
        assertThat(pedido.getMesa()).isEqualTo(mesaBack);

        pedido.mesa(null);
        assertThat(pedido.getMesa()).isNull();
    }
}
