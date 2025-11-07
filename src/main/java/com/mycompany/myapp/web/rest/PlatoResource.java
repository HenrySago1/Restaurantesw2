package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Plato;
import com.mycompany.myapp.repository.PlatoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Plato}.
 */
@RestController
@RequestMapping("/api/platoes")
public class PlatoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlatoResource.class);

    private static final String ENTITY_NAME = "inventarioMenuPlato";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlatoRepository platoRepository;

    public PlatoResource(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    /**
     * {@code POST  /platoes} : Create a new plato.
     *
     * @param plato the plato to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new plato, or with status {@code 400 (Bad Request)} if the plato has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Plato> createPlato(@Valid @RequestBody Plato plato) throws URISyntaxException {
        LOG.debug("REST request to save Plato : {}", plato);
        if (plato.getId() != null) {
            throw new BadRequestAlertException("A new plato cannot already have an ID", ENTITY_NAME, "idexists");
        }
        plato = platoRepository.save(plato);
        return ResponseEntity.created(new URI("/api/platoes/" + plato.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, plato.getId()))
            .body(plato);
    }

    /**
     * {@code PUT  /platoes/:id} : Updates an existing plato.
     *
     * @param id the id of the plato to save.
     * @param plato the plato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plato,
     * or with status {@code 400 (Bad Request)} if the plato is not valid,
     * or with status {@code 500 (Internal Server Error)} if the plato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Plato> updatePlato(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Plato plato
    ) throws URISyntaxException {
        LOG.debug("REST request to update Plato : {}, {}", id, plato);
        if (plato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        plato = platoRepository.save(plato);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plato.getId()))
            .body(plato);
    }

    /**
     * {@code PATCH  /platoes/:id} : Partial updates given fields of an existing plato, field will ignore if it is null
     *
     * @param id the id of the plato to save.
     * @param plato the plato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plato,
     * or with status {@code 400 (Bad Request)} if the plato is not valid,
     * or with status {@code 404 (Not Found)} if the plato is not found,
     * or with status {@code 500 (Internal Server Error)} if the plato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Plato> partialUpdatePlato(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Plato plato
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Plato partially : {}, {}", id, plato);
        if (plato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!platoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Plato> result = platoRepository
            .findById(plato.getId())
            .map(existingPlato -> {
                if (plato.getNombre() != null) {
                    existingPlato.setNombre(plato.getNombre());
                }
                if (plato.getDescripcion() != null) {
                    existingPlato.setDescripcion(plato.getDescripcion());
                }
                if (plato.getPrecio() != null) {
                    existingPlato.setPrecio(plato.getPrecio());
                }
                if (plato.getActivo() != null) {
                    existingPlato.setActivo(plato.getActivo());
                }

                return existingPlato;
            })
            .map(platoRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, plato.getId()));
    }

    /**
     * {@code GET  /platoes} : get all the platoes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of platoes in body.
     */
    @GetMapping("")
    public List<Plato> getAllPlatoes(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Platoes");
        if (eagerload) {
            return platoRepository.findAllWithEagerRelationships();
        } else {
            return platoRepository.findAll();
        }
    }

    /**
     * {@code GET  /platoes/:id} : get the "id" plato.
     *
     * @param id the id of the plato to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plato, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Plato> getPlato(@PathVariable("id") String id) {
        LOG.debug("REST request to get Plato : {}", id);
        Optional<Plato> plato = platoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(plato);
    }

    /**
     * {@code DELETE  /platoes/:id} : delete the "id" plato.
     *
     * @param id the id of the plato to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlato(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Plato : {}", id);
        platoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
