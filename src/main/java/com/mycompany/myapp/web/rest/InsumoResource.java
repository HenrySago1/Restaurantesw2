package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Insumo;
import com.mycompany.myapp.repository.InsumoRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Insumo}.
 */
@RestController
@RequestMapping("/api/insumos")
public class InsumoResource {

    private static final Logger LOG = LoggerFactory.getLogger(InsumoResource.class);

    private static final String ENTITY_NAME = "inventarioMenuInsumo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InsumoRepository insumoRepository;

    public InsumoResource(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    /**
     * {@code POST  /insumos} : Create a new insumo.
     *
     * @param insumo the insumo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new insumo, or with status {@code 400 (Bad Request)} if the insumo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Insumo> createInsumo(@Valid @RequestBody Insumo insumo) throws URISyntaxException {
        LOG.debug("REST request to save Insumo : {}", insumo);
        if (insumo.getId() != null) {
            throw new BadRequestAlertException("A new insumo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        insumo = insumoRepository.save(insumo);
        return ResponseEntity.created(new URI("/api/insumos/" + insumo.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, insumo.getId()))
            .body(insumo);
    }

    /**
     * {@code PUT  /insumos/:id} : Updates an existing insumo.
     *
     * @param id the id of the insumo to save.
     * @param insumo the insumo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insumo,
     * or with status {@code 400 (Bad Request)} if the insumo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the insumo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Insumo> updateInsumo(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Insumo insumo
    ) throws URISyntaxException {
        LOG.debug("REST request to update Insumo : {}, {}", id, insumo);
        if (insumo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insumo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insumoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        insumo = insumoRepository.save(insumo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, insumo.getId()))
            .body(insumo);
    }

    /**
     * {@code PATCH  /insumos/:id} : Partial updates given fields of an existing insumo, field will ignore if it is null
     *
     * @param id the id of the insumo to save.
     * @param insumo the insumo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insumo,
     * or with status {@code 400 (Bad Request)} if the insumo is not valid,
     * or with status {@code 404 (Not Found)} if the insumo is not found,
     * or with status {@code 500 (Internal Server Error)} if the insumo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Insumo> partialUpdateInsumo(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Insumo insumo
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Insumo partially : {}, {}", id, insumo);
        if (insumo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insumo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insumoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Insumo> result = insumoRepository
            .findById(insumo.getId())
            .map(existingInsumo -> {
                if (insumo.getNombre() != null) {
                    existingInsumo.setNombre(insumo.getNombre());
                }
                if (insumo.getStockMinimo() != null) {
                    existingInsumo.setStockMinimo(insumo.getStockMinimo());
                }
                if (insumo.getStockActual() != null) {
                    existingInsumo.setStockActual(insumo.getStockActual());
                }

                return existingInsumo;
            })
            .map(insumoRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, insumo.getId()));
    }

    /**
     * {@code GET  /insumos} : get all the insumos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of insumos in body.
     */
    @GetMapping("")
    public List<Insumo> getAllInsumos() {
        LOG.debug("REST request to get all Insumos");
        return insumoRepository.findAll();
    }

    /**
     * {@code GET  /insumos/:id} : get the "id" insumo.
     *
     * @param id the id of the insumo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the insumo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Insumo> getInsumo(@PathVariable("id") String id) {
        LOG.debug("REST request to get Insumo : {}", id);
        Optional<Insumo> insumo = insumoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(insumo);
    }

    /**
     * {@code DELETE  /insumos/:id} : delete the "id" insumo.
     *
     * @param id the id of the insumo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsumo(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Insumo : {}", id);
        insumoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
