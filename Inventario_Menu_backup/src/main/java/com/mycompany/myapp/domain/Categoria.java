package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Categoria.
 */
@Document(collection = "categoria")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("nombre")
    private String nombre;

    @DBRef
    @Field("platoes")
    @JsonIgnoreProperties(value = { "categorias", "insumos" }, allowSetters = true)
    private Set<Plato> platoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Categoria id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Categoria nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Plato> getPlatoes() {
        return this.platoes;
    }

    public void setPlatoes(Set<Plato> platoes) {
        if (this.platoes != null) {
            this.platoes.forEach(i -> i.removeCategoria(this));
        }
        if (platoes != null) {
            platoes.forEach(i -> i.addCategoria(this));
        }
        this.platoes = platoes;
    }

    public Categoria platoes(Set<Plato> platoes) {
        this.setPlatoes(platoes);
        return this;
    }

    public Categoria addPlato(Plato plato) {
        this.platoes.add(plato);
        plato.getCategorias().add(this);
        return this;
    }

    public Categoria removePlato(Plato plato) {
        this.platoes.remove(plato);
        plato.getCategorias().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria)) {
            return false;
        }
        return getId() != null && getId().equals(((Categoria) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Categoria{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            "}";
    }
}
