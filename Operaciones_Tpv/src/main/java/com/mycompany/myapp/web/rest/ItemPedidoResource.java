package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ItemPedido;
import com.mycompany.myapp.repository.ItemPedidoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ItemPedido}.
 */
@RestController
@RequestMapping("/api/item-pedidos")
@Transactional
public class ItemPedidoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ItemPedidoResource.class);

    private static final String ENTITY_NAME = "operacionesTpvItemPedido";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoResource(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * {@code POST  /item-pedidos} : Create a new itemPedido.
     *
     * @param itemPedido the itemPedido to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemPedido, or with status {@code 400 (Bad Request)} if the itemPedido has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ItemPedido> createItemPedido(@Valid @RequestBody ItemPedido itemPedido) throws URISyntaxException {
        LOG.debug("REST request to save ItemPedido : {}", itemPedido);
        if (itemPedido.getId() != null) {
            throw new BadRequestAlertException("A new itemPedido cannot already have an ID", ENTITY_NAME, "idexists");
        }
        itemPedido = itemPedidoRepository.save(itemPedido);
        return ResponseEntity.created(new URI("/api/item-pedidos/" + itemPedido.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, itemPedido.getId().toString()))
            .body(itemPedido);
    }

    /**
     * {@code PUT  /item-pedidos/:id} : Updates an existing itemPedido.
     *
     * @param id the id of the itemPedido to save.
     * @param itemPedido the itemPedido to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemPedido,
     * or with status {@code 400 (Bad Request)} if the itemPedido is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemPedido couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> updateItemPedido(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ItemPedido itemPedido
    ) throws URISyntaxException {
        LOG.debug("REST request to update ItemPedido : {}, {}", id, itemPedido);
        if (itemPedido.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemPedido.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        itemPedido = itemPedidoRepository.save(itemPedido);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemPedido.getId().toString()))
            .body(itemPedido);
    }

    /**
     * {@code PATCH  /item-pedidos/:id} : Partial updates given fields of an existing itemPedido, field will ignore if it is null
     *
     * @param id the id of the itemPedido to save.
     * @param itemPedido the itemPedido to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemPedido,
     * or with status {@code 400 (Bad Request)} if the itemPedido is not valid,
     * or with status {@code 404 (Not Found)} if the itemPedido is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemPedido couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ItemPedido> partialUpdateItemPedido(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ItemPedido itemPedido
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ItemPedido partially : {}, {}", id, itemPedido);
        if (itemPedido.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemPedido.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ItemPedido> result = itemPedidoRepository
            .findById(itemPedido.getId())
            .map(existingItemPedido -> {
                if (itemPedido.getCantidad() != null) {
                    existingItemPedido.setCantidad(itemPedido.getCantidad());
                }
                if (itemPedido.getPrecioUnitario() != null) {
                    existingItemPedido.setPrecioUnitario(itemPedido.getPrecioUnitario());
                }

                return existingItemPedido;
            })
            .map(itemPedidoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemPedido.getId().toString())
        );
    }

    /**
     * {@code GET  /item-pedidos} : get all the itemPedidos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of itemPedidos in body.
     */
    @GetMapping("")
    public List<ItemPedido> getAllItemPedidos() {
        LOG.debug("REST request to get all ItemPedidos");
        return itemPedidoRepository.findAll();
    }

    /**
     * {@code GET  /item-pedidos/:id} : get the "id" itemPedido.
     *
     * @param id the id of the itemPedido to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemPedido, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> getItemPedido(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ItemPedido : {}", id);
        Optional<ItemPedido> itemPedido = itemPedidoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(itemPedido);
    }

    /**
     * {@code DELETE  /item-pedidos/:id} : delete the "id" itemPedido.
     *
     * @param id the id of the itemPedido to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemPedido(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ItemPedido : {}", id);
        itemPedidoRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
