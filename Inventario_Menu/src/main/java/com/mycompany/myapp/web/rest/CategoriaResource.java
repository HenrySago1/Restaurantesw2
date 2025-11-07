package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Categoria;
import com.mycompany.myapp.repository.CategoriaRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Categoria}.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaResource.class);

    private static final String ENTITY_NAME = "inventarioMenuCategoria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoriaRepository categoriaRepository;

    public CategoriaResource(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * {@code POST  /categorias} : Create a new categoria.
     *
     * @param categoria the categoria to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoria, or with status {@code 400 (Bad Request)} if the categoria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Categoria> createCategoria(@Valid @RequestBody Categoria categoria) throws URISyntaxException {
        LOG.debug("REST request to save Categoria : {}", categoria);
        if (categoria.getId() != null) {
            throw new BadRequestAlertException("A new categoria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        categoria = categoriaRepository.save(categoria);
        return ResponseEntity.created(new URI("/api/categorias/" + categoria.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, categoria.getId()))
            .body(categoria);
    }

    /**
     * {@code PUT  /categorias/:id} : Updates an existing categoria.
     *
     * @param id the id of the categoria to save.
     * @param categoria the categoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoria,
     * or with status {@code 400 (Bad Request)} if the categoria is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Categoria categoria
    ) throws URISyntaxException {
        LOG.debug("REST request to update Categoria : {}, {}", id, categoria);
        if (categoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        categoria = categoriaRepository.save(categoria);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoria.getId()))
            .body(categoria);
    }

    /**
     * {@code PATCH  /categorias/:id} : Partial updates given fields of an existing categoria, field will ignore if it is null
     *
     * @param id the id of the categoria to save.
     * @param categoria the categoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoria,
     * or with status {@code 400 (Bad Request)} if the categoria is not valid,
     * or with status {@code 404 (Not Found)} if the categoria is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Categoria> partialUpdateCategoria(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Categoria categoria
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Categoria partially : {}, {}", id, categoria);
        if (categoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Categoria> result = categoriaRepository
            .findById(categoria.getId())
            .map(existingCategoria -> {
                if (categoria.getNombre() != null) {
                    existingCategoria.setNombre(categoria.getNombre());
                }

                return existingCategoria;
            })
            .map(categoriaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoria.getId())
        );
    }

    /**
     * {@code GET  /categorias} : get all the categorias.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categorias in body.
     */
    @GetMapping("")
    public List<Categoria> getAllCategorias() {
        LOG.debug("REST request to get all Categorias");
        return categoriaRepository.findAll();
    }

    /**
     * {@code GET  /categorias/:id} : get the "id" categoria.
     *
     * @param id the id of the categoria to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoria, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoria(@PathVariable("id") String id) {
        LOG.debug("REST request to get Categoria : {}", id);
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(categoria);
    }

    /**
     * {@code DELETE  /categorias/:id} : delete the "id" categoria.
     *
     * @param id the id of the categoria to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Categoria : {}", id);
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
