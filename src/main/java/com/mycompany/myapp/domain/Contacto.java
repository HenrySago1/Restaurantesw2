package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Contacto.
 */
@Document(collection = "contacto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Contacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Field("fecha_contacto")
    private ZonedDateTime fechaContacto;

    @NotNull
    @Field("motivo")
    private String motivo;

    @DBRef
    @Field("cliente")
    @JsonIgnoreProperties(value = { "reservas", "contactos" }, allowSetters = true)
    private Cliente cliente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Contacto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFechaContacto() {
        return this.fechaContacto;
    }

    public Contacto fechaContacto(ZonedDateTime fechaContacto) {
        this.setFechaContacto(fechaContacto);
        return this;
    }

    public void setFechaContacto(ZonedDateTime fechaContacto) {
        this.fechaContacto = fechaContacto;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public Contacto motivo(String motivo) {
        this.setMotivo(motivo);
        return this;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Contacto cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contacto)) {
            return false;
        }
        return getId() != null && getId().equals(((Contacto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Contacto{" +
            "id=" + getId() +
            ", fechaContacto='" + getFechaContacto() + "'" +
            ", motivo='" + getMotivo() + "'" +
            "}";
    }
}
