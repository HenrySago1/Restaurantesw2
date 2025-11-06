package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Mesa;
import com.mycompany.myapp.repository.MesaRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Mesa}.
 */
@RestController
@RequestMapping("/api/mesas")
@Transactional
public class MesaResource {

    private static final Logger LOG = LoggerFactory.getLogger(MesaResource.class);

    private static final String ENTITY_NAME = "operacionesTpvMesa";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MesaRepository mesaRepository;

    public MesaResource(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    /**
     * {@code POST  /mesas} : Create a new mesa.
     *
     * @param mesa the mesa to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mesa, or with status {@code 400 (Bad Request)} if the mesa has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Mesa> createMesa(@Valid @RequestBody Mesa mesa) throws URISyntaxException {
        LOG.debug("REST request to save Mesa : {}", mesa);
        if (mesa.getId() != null) {
            throw new BadRequestAlertException("A new mesa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mesa = mesaRepository.save(mesa);
        return ResponseEntity.created(new URI("/api/mesas/" + mesa.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mesa.getId().toString()))
            .body(mesa);
    }

    /**
     * {@code PUT  /mesas/:id} : Updates an existing mesa.
     *
     * @param id the id of the mesa to save.
     * @param mesa the mesa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mesa,
     * or with status {@code 400 (Bad Request)} if the mesa is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mesa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mesa> updateMesa(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Mesa mesa)
        throws URISyntaxException {
        LOG.debug("REST request to update Mesa : {}, {}", id, mesa);
        if (mesa.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mesa.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mesaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mesa = mesaRepository.save(mesa);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mesa.getId().toString()))
            .body(mesa);
    }

    /**
     * {@code PATCH  /mesas/:id} : Partial updates given fields of an existing mesa, field will ignore if it is null
     *
     * @param id the id of the mesa to save.
     * @param mesa the mesa to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mesa,
     * or with status {@code 400 (Bad Request)} if the mesa is not valid,
     * or with status {@code 404 (Not Found)} if the mesa is not found,
     * or with status {@code 500 (Internal Server Error)} if the mesa couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mesa> partialUpdateMesa(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Mesa mesa
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Mesa partially : {}, {}", id, mesa);
        if (mesa.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mesa.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mesaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mesa> result = mesaRepository
            .findById(mesa.getId())
            .map(existingMesa -> {
                if (mesa.getNumero() != null) {
                    existingMesa.setNumero(mesa.getNumero());
                }
                if (mesa.getCapacidad() != null) {
                    existingMesa.setCapacidad(mesa.getCapacidad());
                }
                if (mesa.getEstado() != null) {
                    existingMesa.setEstado(mesa.getEstado());
                }

                return existingMesa;
            })
            .map(mesaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mesa.getId().toString())
        );
    }

    /**
     * {@code GET  /mesas} : get all the mesas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mesas in body.
     */
    @GetMapping("")
    public List<Mesa> getAllMesas() {
        LOG.debug("REST request to get all Mesas");
        return mesaRepository.findAll();
    }

    /**
     * {@code GET  /mesas/:id} : get the "id" mesa.
     *
     * @param id the id of the mesa to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mesa, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mesa> getMesa(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Mesa : {}", id);
        Optional<Mesa> mesa = mesaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mesa);
    }

    /**
     * {@code DELETE  /mesas/:id} : delete the "id" mesa.
     *
     * @param id the id of the mesa to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Mesa : {}", id);
        mesaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
