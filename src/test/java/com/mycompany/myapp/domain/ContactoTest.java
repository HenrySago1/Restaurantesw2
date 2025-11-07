package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClienteTestSamples.*;
import static com.mycompany.myapp.domain.ContactoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contacto.class);
        Contacto contacto1 = getContactoSample1();
        Contacto contacto2 = new Contacto();
        assertThat(contacto1).isNotEqualTo(contacto2);

        contacto2.setId(contacto1.getId());
        assertThat(contacto1).isEqualTo(contacto2);

        contacto2 = getContactoSample2();
        assertThat(contacto1).isNotEqualTo(contacto2);
    }

    @Test
    void clienteTest() {
        Contacto contacto = getContactoRandomSampleGenerator();
        Cliente clienteBack = getClienteRandomSampleGenerator();

        contacto.setCliente(clienteBack);
        assertThat(contacto.getCliente()).isEqualTo(clienteBack);

        contacto.cliente(null);
        assertThat(contacto.getCliente()).isNull();
    }
}
