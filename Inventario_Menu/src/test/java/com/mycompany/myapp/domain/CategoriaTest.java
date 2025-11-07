package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CategoriaTestSamples.*;
import static com.mycompany.myapp.domain.PlatoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Categoria.class);
        Categoria categoria1 = getCategoriaSample1();
        Categoria categoria2 = new Categoria();
        assertThat(categoria1).isNotEqualTo(categoria2);

        categoria2.setId(categoria1.getId());
        assertThat(categoria1).isEqualTo(categoria2);

        categoria2 = getCategoriaSample2();
        assertThat(categoria1).isNotEqualTo(categoria2);
    }

    @Test
    void platoTest() {
        Categoria categoria = getCategoriaRandomSampleGenerator();
        Plato platoBack = getPlatoRandomSampleGenerator();

        categoria.addPlato(platoBack);
        assertThat(categoria.getPlatoes()).containsOnly(platoBack);
        assertThat(platoBack.getCategorias()).containsOnly(categoria);

        categoria.removePlato(platoBack);
        assertThat(categoria.getPlatoes()).doesNotContain(platoBack);
        assertThat(platoBack.getCategorias()).doesNotContain(categoria);

        categoria.platoes(new HashSet<>(Set.of(platoBack)));
        assertThat(categoria.getPlatoes()).containsOnly(platoBack);
        assertThat(platoBack.getCategorias()).containsOnly(categoria);

        categoria.setPlatoes(new HashSet<>());
        assertThat(categoria.getPlatoes()).doesNotContain(platoBack);
        assertThat(platoBack.getCategorias()).doesNotContain(categoria);
    }
}
