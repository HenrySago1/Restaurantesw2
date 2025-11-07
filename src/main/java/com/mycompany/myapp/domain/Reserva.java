package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.EstadoReserva;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Reserva.
 */
@Document(collection = "reserva")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull
    @Field("fecha_hora")
    private ZonedDateTime fechaHora;

    @Min(value = 1)
    @Max(value = 30)
    @Field("personas")
    private Integer personas;

    @NotNull
    @Field("estado")
    private EstadoReserva estado;

    @Field("observaciones")
    private String observaciones;

    @DBRef
    @Field("cliente")
    @JsonIgnoreProperties(value = { "reservas", "contactos" }, allowSetters = true)
    private Cliente cliente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reserva id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFechaHora() {
        return this.fechaHora;
    }

    public Reserva fechaHora(ZonedDateTime fechaHora) {
        this.setFechaHora(fechaHora);
        return this;
    }

    public void setFechaHora(ZonedDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getPersonas() {
        return this.personas;
    }

    public Reserva personas(Integer personas) {
        this.setPersonas(personas);
        return this;
    }

    public void setPersonas(Integer personas) {
        this.personas = personas;
    }

    public EstadoReserva getEstado() {
        return this.estado;
    }

    public Reserva estado(EstadoReserva estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public Reserva observaciones(String observaciones) {
        this.setObservaciones(observaciones);
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Reserva cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva)) {
            return false;
        }
        return getId() != null && getId().equals(((Reserva) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reserva{" +
            "id=" + getId() +
            ", fechaHora='" + getFechaHora() + "'" +
            ", personas=" + getPersonas() +
            ", estado='" + getEstado() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            "}";
    }
}
