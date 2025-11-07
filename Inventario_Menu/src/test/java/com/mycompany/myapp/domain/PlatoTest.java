package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CategoriaTestSamples.*;
import static com.mycompany.myapp.domain.InsumoTestSamples.*;
import static com.mycompany.myapp.domain.PlatoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlatoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plato.class);
        Plato plato1 = getPlatoSample1();
        Plato plato2 = new Plato();
        assertThat(plato1).isNotEqualTo(plato2);

        plato2.setId(plato1.getId());
        assertThat(plato1).isEqualTo(plato2);

        plato2 = getPlatoSample2();
        assertThat(plato1).isNotEqualTo(plato2);
    }

    @Test
    void categoriaTest() {
        Plato plato = getPlatoRandomSampleGenerator();
        Categoria categoriaBack = getCategoriaRandomSampleGenerator();

        plato.addCategoria(categoriaBack);
        assertThat(plato.getCategorias()).containsOnly(categoriaBack);

        plato.removeCategoria(categoriaBack);
        assertThat(plato.getCategorias()).doesNotContain(categoriaBack);

        plato.categorias(new HashSet<>(Set.of(categoriaBack)));
        assertThat(plato.getCategorias()).containsOnly(categoriaBack);

        plato.setCategorias(new HashSet<>());
        assertThat(plato.getCategorias()).doesNotContain(categoriaBack);
    }

    @Test
    void insumoTest() {
        Plato plato = getPlatoRandomSampleGenerator();
        Insumo insumoBack = getInsumoRandomSampleGenerator();

        plato.addInsumo(insumoBack);
        assertThat(plato.getInsumos()).containsOnly(insumoBack);
        assertThat(insumoBack.getPlato()).isEqualTo(plato);

        plato.removeInsumo(insumoBack);
        assertThat(plato.getInsumos()).doesNotContain(insumoBack);
        assertThat(insumoBack.getPlato()).isNull();

        plato.insumos(new HashSet<>(Set.of(insumoBack)));
        assertThat(plato.getInsumos()).containsOnly(insumoBack);
        assertThat(insumoBack.getPlato()).isEqualTo(plato);

        plato.setInsumos(new HashSet<>());
        assertThat(plato.getInsumos()).doesNotContain(insumoBack);
        assertThat(insumoBack.getPlato()).isNull();
    }
}
