package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MesaTestSamples.*;
import static com.mycompany.myapp.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MesaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mesa.class);
        Mesa mesa1 = getMesaSample1();
        Mesa mesa2 = new Mesa();
        assertThat(mesa1).isNotEqualTo(mesa2);

        mesa2.setId(mesa1.getId());
        assertThat(mesa1).isEqualTo(mesa2);

        mesa2 = getMesaSample2();
        assertThat(mesa1).isNotEqualTo(mesa2);
    }

    @Test
    void pedidoTest() {
        Mesa mesa = getMesaRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        mesa.addPedido(pedidoBack);
        assertThat(mesa.getPedidos()).containsOnly(pedidoBack);
        assertThat(pedidoBack.getMesa()).isEqualTo(mesa);

        mesa.removePedido(pedidoBack);
        assertThat(mesa.getPedidos()).doesNotContain(pedidoBack);
        assertThat(pedidoBack.getMesa()).isNull();

        mesa.pedidos(new HashSet<>(Set.of(pedidoBack)));
        assertThat(mesa.getPedidos()).containsOnly(pedidoBack);
        assertThat(pedidoBack.getMesa()).isEqualTo(mesa);

        mesa.setPedidos(new HashSet<>());
        assertThat(mesa.getPedidos()).doesNotContain(pedidoBack);
        assertThat(pedidoBack.getMesa()).isNull();
    }
}
