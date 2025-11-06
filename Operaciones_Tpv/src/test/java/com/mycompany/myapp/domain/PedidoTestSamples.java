package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PedidoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Pedido getPedidoSample1() {
        return new Pedido().id(1L);
    }

    public static Pedido getPedidoSample2() {
        return new Pedido().id(2L);
    }

    public static Pedido getPedidoRandomSampleGenerator() {
        return new Pedido().id(longCount.incrementAndGet());
    }
}
