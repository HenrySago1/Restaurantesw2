package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReservaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Reserva getReservaSample1() {
        return new Reserva().id(1L).personas(1).observaciones("observaciones1");
    }

    public static Reserva getReservaSample2() {
        return new Reserva().id(2L).personas(2).observaciones("observaciones2");
    }

    public static Reserva getReservaRandomSampleGenerator() {
        return new Reserva()
            .id(longCount.incrementAndGet())
            .personas(intCount.incrementAndGet())
            .observaciones(UUID.randomUUID().toString());
    }
}
