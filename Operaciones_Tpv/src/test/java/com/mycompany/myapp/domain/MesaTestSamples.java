package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MesaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Mesa getMesaSample1() {
        return new Mesa().id(1L).numero(1).capacidad(1);
    }

    public static Mesa getMesaSample2() {
        return new Mesa().id(2L).numero(2).capacidad(2);
    }

    public static Mesa getMesaRandomSampleGenerator() {
        return new Mesa().id(longCount.incrementAndGet()).numero(intCount.incrementAndGet()).capacidad(intCount.incrementAndGet());
    }
}
