package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FacturaTestSamples.*;
import static com.mycompany.myapp.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FacturaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Factura.class);
        Factura factura1 = getFacturaSample1();
        Factura factura2 = new Factura();
        assertThat(factura1).isNotEqualTo(factura2);

        factura2.setId(factura1.getId());
        assertThat(factura1).isEqualTo(factura2);

        factura2 = getFacturaSample2();
        assertThat(factura1).isNotEqualTo(factura2);
    }

    @Test
    void pedidoTest() {
        Factura factura = getFacturaRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        factura.setPedido(pedidoBack);
        assertThat(factura.getPedido()).isEqualTo(pedidoBack);
        assertThat(pedidoBack.getFactura()).isEqualTo(factura);

        factura.pedido(null);
        assertThat(factura.getPedido()).isNull();
        assertThat(pedidoBack.getFactura()).isNull();
    }
}
