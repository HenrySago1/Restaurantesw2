package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ItemPedidoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ItemPedido getItemPedidoSample1() {
        return new ItemPedido().id(1L).cantidad(1);
    }

    public static ItemPedido getItemPedidoSample2() {
        return new ItemPedido().id(2L).cantidad(2);
    }

    public static ItemPedido getItemPedidoRandomSampleGenerator() {
        return new ItemPedido().id(longCount.incrementAndGet()).cantidad(intCount.incrementAndGet());
    }
}
