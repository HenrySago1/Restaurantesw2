package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Plato.
 */
@Document(collection = "plato")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Plato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("nombre")
    private String nombre;

    @Field("descripcion")
    private String descripcion;

    @NotNull
    @Field("precio")
    private BigDecimal precio;

    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("categorias")
    @JsonIgnoreProperties(value = { "platoes" }, allowSetters = true)
    private Set<Categoria> categorias = new HashSet<>();

    @DBRef
    @Field("insumo")
    @JsonIgnoreProperties(value = { "plato" }, allowSetters = true)
    private Set<Insumo> insumos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Plato id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Plato nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Plato descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return this.precio;
    }

    public Plato precio(BigDecimal precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Plato activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<Categoria> getCategorias() {
        return this.categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Plato categorias(Set<Categoria> categorias) {
        this.setCategorias(categorias);
        return this;
    }

    public Plato addCategoria(Categoria categoria) {
        this.categorias.add(categoria);
        return this;
    }

    public Plato removeCategoria(Categoria categoria) {
        this.categorias.remove(categoria);
        return this;
    }

    public Set<Insumo> getInsumos() {
        return this.insumos;
    }

    public void setInsumos(Set<Insumo> insumos) {
        if (this.insumos != null) {
            this.insumos.forEach(i -> i.setPlato(null));
        }
        if (insumos != null) {
            insumos.forEach(i -> i.setPlato(this));
        }
        this.insumos = insumos;
    }

    public Plato insumos(Set<Insumo> insumos) {
        this.setInsumos(insumos);
        return this;
    }

    public Plato addInsumo(Insumo insumo) {
        this.insumos.add(insumo);
        insumo.setPlato(this);
        return this;
    }

    public Plato removeInsumo(Insumo insumo) {
        this.insumos.remove(insumo);
        insumo.setPlato(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plato)) {
            return false;
        }
        return getId() != null && getId().equals(((Plato) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plato{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", precio=" + getPrecio() +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
