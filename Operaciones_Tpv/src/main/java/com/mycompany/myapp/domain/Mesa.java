package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.EstadoMesa;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Mesa.
 */
@Entity
@Table(name = "mesa")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Mesa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "numero", nullable = false, unique = true)
    private Integer numero;

    @NotNull
    @Min(value = 1)
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMesa estado;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mesa")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "factura", "itemPedidos", "mesa" }, allowSetters = true)
    private Set<Pedido> pedidos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Mesa id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Mesa numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCapacidad() {
        return this.capacidad;
    }

    public Mesa capacidad(Integer capacidad) {
        this.setCapacidad(capacidad);
        return this;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoMesa getEstado() {
        return this.estado;
    }

    public Mesa estado(EstadoMesa estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }

    public Set<Pedido> getPedidos() {
        return this.pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        if (this.pedidos != null) {
            this.pedidos.forEach(i -> i.setMesa(null));
        }
        if (pedidos != null) {
            pedidos.forEach(i -> i.setMesa(this));
        }
        this.pedidos = pedidos;
    }

    public Mesa pedidos(Set<Pedido> pedidos) {
        this.setPedidos(pedidos);
        return this;
    }

    public Mesa addPedido(Pedido pedido) {
        this.pedidos.add(pedido);
        pedido.setMesa(this);
        return this;
    }

    public Mesa removePedido(Pedido pedido) {
        this.pedidos.remove(pedido);
        pedido.setMesa(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mesa)) {
            return false;
        }
        return getId() != null && getId().equals(((Mesa) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mesa{" +
            "id=" + getId() +
            ", numero=" + getNumero() +
            ", capacidad=" + getCapacidad() +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
