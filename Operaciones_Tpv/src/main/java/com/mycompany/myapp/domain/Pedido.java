package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.EstadoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pedido.
 */
@Entity
@Table(name = "pedido")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha_pedido", nullable = false)
    private ZonedDateTime fechaPedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;

    @JsonIgnoreProperties(value = { "pedido" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Factura factura;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "pedido" }, allowSetters = true)
    private Set<ItemPedido> itemPedidos = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "pedidos" }, allowSetters = true)
    private Mesa mesa;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pedido id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFechaPedido() {
        return this.fechaPedido;
    }

    public Pedido fechaPedido(ZonedDateTime fechaPedido) {
        this.setFechaPedido(fechaPedido);
        return this;
    }

    public void setFechaPedido(ZonedDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public EstadoPedido getEstado() {
        return this.estado;
    }

    public Pedido estado(EstadoPedido estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Factura getFactura() {
        return this.factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Pedido factura(Factura factura) {
        this.setFactura(factura);
        return this;
    }

    public Set<ItemPedido> getItemPedidos() {
        return this.itemPedidos;
    }

    public void setItemPedidos(Set<ItemPedido> itemPedidos) {
        if (this.itemPedidos != null) {
            this.itemPedidos.forEach(i -> i.setPedido(null));
        }
        if (itemPedidos != null) {
            itemPedidos.forEach(i -> i.setPedido(this));
        }
        this.itemPedidos = itemPedidos;
    }

    public Pedido itemPedidos(Set<ItemPedido> itemPedidos) {
        this.setItemPedidos(itemPedidos);
        return this;
    }

    public Pedido addItemPedido(ItemPedido itemPedido) {
        this.itemPedidos.add(itemPedido);
        itemPedido.setPedido(this);
        return this;
    }

    public Pedido removeItemPedido(ItemPedido itemPedido) {
        this.itemPedidos.remove(itemPedido);
        itemPedido.setPedido(null);
        return this;
    }

    public Mesa getMesa() {
        return this.mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Pedido mesa(Mesa mesa) {
        this.setMesa(mesa);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido)) {
            return false;
        }
        return getId() != null && getId().equals(((Pedido) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pedido{" +
            "id=" + getId() +
            ", fechaPedido='" + getFechaPedido() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
