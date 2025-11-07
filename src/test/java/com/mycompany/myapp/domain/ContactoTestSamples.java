package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Contacto getContactoSample1() {
        return new Contacto().id(1L).motivo("motivo1");
    }

    public static Contacto getContactoSample2() {
        return new Contacto().id(2L).motivo("motivo2");
    }

    public static Contacto getContactoRandomSampleGenerator() {
        return new Contacto().id(longCount.incrementAndGet()).motivo(UUID.randomUUID().toString());
    }
}
