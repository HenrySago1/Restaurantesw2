package com.mycompany.myapp.domain;

import java.util.UUID;

public class PlatoTestSamples {

    public static Plato getPlatoSample1() {
        return new Plato().id("id1").nombre("nombre1").descripcion("descripcion1");
    }

    public static Plato getPlatoSample2() {
        return new Plato().id("id2").nombre("nombre2").descripcion("descripcion2");
    }

    public static Plato getPlatoRandomSampleGenerator() {
        return new Plato().id(UUID.randomUUID().toString()).nombre(UUID.randomUUID().toString()).descripcion(UUID.randomUUID().toString());
    }
}
