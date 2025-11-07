package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Insumo.
 */
@Document(collection = "insumo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Insumo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("nombre")
    private String nombre;

    @Field("stock_minimo")
    private Integer stockMinimo;

    @Field("stock_actual")
    private Integer stockActual;

    @DBRef
    @Field("plato")
    @JsonIgnoreProperties(value = { "categorias", "insumos" }, allowSetters = true)
    private Plato plato;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Insumo id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Insumo nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStockMinimo() {
        return this.stockMinimo;
    }

    public Insumo stockMinimo(Integer stockMinimo) {
        this.setStockMinimo(stockMinimo);
        return this;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockActual() {
        return this.stockActual;
    }

    public Insumo stockActual(Integer stockActual) {
        this.setStockActual(stockActual);
        return this;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Plato getPlato() {
        return this.plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Insumo plato(Plato plato) {
        this.setPlato(plato);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Insumo)) {
            return false;
        }
        return getId() != null && getId().equals(((Insumo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Insumo{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", stockMinimo=" + getStockMinimo() +
            ", stockActual=" + getStockActual() +
            "}";
    }
}
