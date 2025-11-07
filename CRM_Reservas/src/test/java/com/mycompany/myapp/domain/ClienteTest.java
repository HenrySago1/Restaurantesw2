package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClienteTestSamples.*;
import static com.mycompany.myapp.domain.ContactoTestSamples.*;
import static com.mycompany.myapp.domain.ReservaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cliente.class);
        Cliente cliente1 = getClienteSample1();
        Cliente cliente2 = new Cliente();
        assertThat(cliente1).isNotEqualTo(cliente2);

        cliente2.setId(cliente1.getId());
        assertThat(cliente1).isEqualTo(cliente2);

        cliente2 = getClienteSample2();
        assertThat(cliente1).isNotEqualTo(cliente2);
    }

    @Test
    void reservaTest() {
        Cliente cliente = getClienteRandomSampleGenerator();
        Reserva reservaBack = getReservaRandomSampleGenerator();

        cliente.addReserva(reservaBack);
        assertThat(cliente.getReservas()).containsOnly(reservaBack);
        assertThat(reservaBack.getCliente()).isEqualTo(cliente);

        cliente.removeReserva(reservaBack);
        assertThat(cliente.getReservas()).doesNotContain(reservaBack);
        assertThat(reservaBack.getCliente()).isNull();

        cliente.reservas(new HashSet<>(Set.of(reservaBack)));
        assertThat(cliente.getReservas()).containsOnly(reservaBack);
        assertThat(reservaBack.getCliente()).isEqualTo(cliente);

        cliente.setReservas(new HashSet<>());
        assertThat(cliente.getReservas()).doesNotContain(reservaBack);
        assertThat(reservaBack.getCliente()).isNull();
    }

    @Test
    void contactoTest() {
        Cliente cliente = getClienteRandomSampleGenerator();
        Contacto contactoBack = getContactoRandomSampleGenerator();

        cliente.addContacto(contactoBack);
        assertThat(cliente.getContactos()).containsOnly(contactoBack);
        assertThat(contactoBack.getCliente()).isEqualTo(cliente);

        cliente.removeContacto(contactoBack);
        assertThat(cliente.getContactos()).doesNotContain(contactoBack);
        assertThat(contactoBack.getCliente()).isNull();

        cliente.contactos(new HashSet<>(Set.of(contactoBack)));
        assertThat(cliente.getContactos()).containsOnly(contactoBack);
        assertThat(contactoBack.getCliente()).isEqualTo(cliente);

        cliente.setContactos(new HashSet<>());
        assertThat(cliente.getContactos()).doesNotContain(contactoBack);
        assertThat(contactoBack.getCliente()).isNull();
    }
}
