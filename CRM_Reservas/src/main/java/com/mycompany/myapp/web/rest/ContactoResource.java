package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Contacto;
import com.mycompany.myapp.repository.ContactoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Contacto}.
 */
@RestController
@RequestMapping("/api/contactos")
@Transactional
public class ContactoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContactoResource.class);

    private static final String ENTITY_NAME = "crmReservasContacto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContactoRepository contactoRepository;

    public ContactoResource(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    /**
     * {@code POST  /contactos} : Create a new contacto.
     *
     * @param contacto the contacto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contacto, or with status {@code 400 (Bad Request)} if the contacto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Contacto> createContacto(@Valid @RequestBody Contacto contacto) throws URISyntaxException {
        LOG.debug("REST request to save Contacto : {}", contacto);
        if (contacto.getId() != null) {
            throw new BadRequestAlertException("A new contacto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contacto = contactoRepository.save(contacto);
        return ResponseEntity.created(new URI("/api/contactos/" + contacto.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, contacto.getId().toString()))
            .body(contacto);
    }

    /**
     * {@code PUT  /contactos/:id} : Updates an existing contacto.
     *
     * @param id the id of the contacto to save.
     * @param contacto the contacto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contacto,
     * or with status {@code 400 (Bad Request)} if the contacto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contacto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contacto> updateContacto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Contacto contacto
    ) throws URISyntaxException {
        LOG.debug("REST request to update Contacto : {}, {}", id, contacto);
        if (contacto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contacto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contacto = contactoRepository.save(contacto);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contacto.getId().toString()))
            .body(contacto);
    }

    /**
     * {@code PATCH  /contactos/:id} : Partial updates given fields of an existing contacto, field will ignore if it is null
     *
     * @param id the id of the contacto to save.
     * @param contacto the contacto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contacto,
     * or with status {@code 400 (Bad Request)} if the contacto is not valid,
     * or with status {@code 404 (Not Found)} if the contacto is not found,
     * or with status {@code 500 (Internal Server Error)} if the contacto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Contacto> partialUpdateContacto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Contacto contacto
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Contacto partially : {}, {}", id, contacto);
        if (contacto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contacto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Contacto> result = contactoRepository
            .findById(contacto.getId())
            .map(existingContacto -> {
                if (contacto.getFechaContacto() != null) {
                    existingContacto.setFechaContacto(contacto.getFechaContacto());
                }
                if (contacto.getMotivo() != null) {
                    existingContacto.setMotivo(contacto.getMotivo());
                }

                return existingContacto;
            })
            .map(contactoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contacto.getId().toString())
        );
    }

    /**
     * {@code GET  /contactos} : get all the contactos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contactos in body.
     */
    @GetMapping("")
    public List<Contacto> getAllContactos() {
        LOG.debug("REST request to get all Contactos");
        return contactoRepository.findAll();
    }

    /**
     * {@code GET  /contactos/:id} : get the "id" contacto.
     *
     * @param id the id of the contacto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contacto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contacto> getContacto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Contacto : {}", id);
        Optional<Contacto> contacto = contactoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(contacto);
    }

    /**
     * {@code DELETE  /contactos/:id} : delete the "id" contacto.
     *
     * @param id the id of the contacto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContacto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Contacto : {}", id);
        contactoRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
