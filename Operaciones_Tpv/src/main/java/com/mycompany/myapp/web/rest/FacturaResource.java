package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Factura;
import com.mycompany.myapp.repository.FacturaRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Factura}.
 */
@RestController
@RequestMapping("/api/facturas")
@Transactional
public class FacturaResource {

    private static final Logger LOG = LoggerFactory.getLogger(FacturaResource.class);

    private static final String ENTITY_NAME = "operacionesTpvFactura";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacturaRepository facturaRepository;

    public FacturaResource(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    /**
     * {@code POST  /facturas} : Create a new factura.
     *
     * @param factura the factura to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new factura, or with status {@code 400 (Bad Request)} if the factura has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Factura> createFactura(@Valid @RequestBody Factura factura) throws URISyntaxException {
        LOG.debug("REST request to save Factura : {}", factura);
        if (factura.getId() != null) {
            throw new BadRequestAlertException("A new factura cannot already have an ID", ENTITY_NAME, "idexists");
        }
        factura = facturaRepository.save(factura);
        return ResponseEntity.created(new URI("/api/facturas/" + factura.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, factura.getId().toString()))
            .body(factura);
    }

    /**
     * {@code PUT  /facturas/:id} : Updates an existing factura.
     *
     * @param id the id of the factura to save.
     * @param factura the factura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factura,
     * or with status {@code 400 (Bad Request)} if the factura is not valid,
     * or with status {@code 500 (Internal Server Error)} if the factura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Factura> updateFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Factura factura
    ) throws URISyntaxException {
        LOG.debug("REST request to update Factura : {}, {}", id, factura);
        if (factura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        factura = facturaRepository.save(factura);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, factura.getId().toString()))
            .body(factura);
    }

    /**
     * {@code PATCH  /facturas/:id} : Partial updates given fields of an existing factura, field will ignore if it is null
     *
     * @param id the id of the factura to save.
     * @param factura the factura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factura,
     * or with status {@code 400 (Bad Request)} if the factura is not valid,
     * or with status {@code 404 (Not Found)} if the factura is not found,
     * or with status {@code 500 (Internal Server Error)} if the factura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Factura> partialUpdateFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Factura factura
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Factura partially : {}, {}", id, factura);
        if (factura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Factura> result = facturaRepository
            .findById(factura.getId())
            .map(existingFactura -> {
                if (factura.getFechaFactura() != null) {
                    existingFactura.setFechaFactura(factura.getFechaFactura());
                }
                if (factura.getMontoTotal() != null) {
                    existingFactura.setMontoTotal(factura.getMontoTotal());
                }
                if (factura.getMetodoPago() != null) {
                    existingFactura.setMetodoPago(factura.getMetodoPago());
                }

                return existingFactura;
            })
            .map(facturaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, factura.getId().toString())
        );
    }

    /**
     * {@code GET  /facturas} : get all the facturas.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of facturas in body.
     */
    @GetMapping("")
    public List<Factura> getAllFacturas(@RequestParam(name = "filter", required = false) String filter) {
        if ("pedido-is-null".equals(filter)) {
            LOG.debug("REST request to get all Facturas where pedido is null");
            return StreamSupport.stream(facturaRepository.findAll().spliterator(), false)
                .filter(factura -> factura.getPedido() == null)
                .toList();
        }
        LOG.debug("REST request to get all Facturas");
        return facturaRepository.findAll();
    }

    /**
     * {@code GET  /facturas/:id} : get the "id" factura.
     *
     * @param id the id of the factura to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the factura, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFactura(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Factura : {}", id);
        Optional<Factura> factura = facturaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(factura);
    }

    /**
     * {@code DELETE  /facturas/:id} : delete the "id" factura.
     *
     * @param id the id of the factura to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Factura : {}", id);
        facturaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
