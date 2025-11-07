package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class InsumoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Insumo getInsumoSample1() {
        return new Insumo().id("id1").nombre("nombre1").stockMinimo(1).stockActual(1);
    }

    public static Insumo getInsumoSample2() {
        return new Insumo().id("id2").nombre("nombre2").stockMinimo(2).stockActual(2);
    }

    public static Insumo getInsumoRandomSampleGenerator() {
        return new Insumo()
            .id(UUID.randomUUID().toString())
            .nombre(UUID.randomUUID().toString())
            .stockMinimo(intCount.incrementAndGet())
            .stockActual(intCount.incrementAndGet());
    }
}
