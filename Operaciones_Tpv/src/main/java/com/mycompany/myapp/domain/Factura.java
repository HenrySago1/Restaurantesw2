package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Factura.
 */
@Entity
@Table(name = "factura")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha_factura", nullable = false)
    private ZonedDateTime fechaFactura;

    @NotNull
    @Column(name = "monto_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @JsonIgnoreProperties(value = { "factura", "itemPedidos", "mesa" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "factura")
    private Pedido pedido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Factura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFechaFactura() {
        return this.fechaFactura;
    }

    public Factura fechaFactura(ZonedDateTime fechaFactura) {
        this.setFechaFactura(fechaFactura);
        return this;
    }

    public void setFechaFactura(ZonedDateTime fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public BigDecimal getMontoTotal() {
        return this.montoTotal;
    }

    public Factura montoTotal(BigDecimal montoTotal) {
        this.setMontoTotal(montoTotal);
        return this;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public MetodoPago getMetodoPago() {
        return this.metodoPago;
    }

    public Factura metodoPago(MetodoPago metodoPago) {
        this.setMetodoPago(metodoPago);
        return this;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        if (this.pedido != null) {
            this.pedido.setFactura(null);
        }
        if (pedido != null) {
            pedido.setFactura(this);
        }
        this.pedido = pedido;
    }

    public Factura pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return getId() != null && getId().equals(((Factura) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
            ", fechaFactura='" + getFechaFactura() + "'" +
            ", montoTotal=" + getMontoTotal() +
            ", metodoPago='" + getMetodoPago() + "'" +
            "}";
    }
}
