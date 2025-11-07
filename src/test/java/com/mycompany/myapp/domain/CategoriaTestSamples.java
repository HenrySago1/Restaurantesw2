package com.mycompany.myapp.domain;

import java.util.UUID;

public class CategoriaTestSamples {

    public static Categoria getCategoriaSample1() {
        return new Categoria().id("id1").nombre("nombre1");
    }

    public static Categoria getCategoriaSample2() {
        return new Categoria().id("id2").nombre("nombre2");
    }

    public static Categoria getCategoriaRandomSampleGenerator() {
        return new Categoria().id(UUID.randomUUID().toString()).nombre(UUID.randomUUID().toString());
    }
}
