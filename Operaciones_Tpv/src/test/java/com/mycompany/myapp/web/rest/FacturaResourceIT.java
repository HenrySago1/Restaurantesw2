package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FacturaAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Factura;
import com.mycompany.myapp.domain.enumeration.MetodoPago;
import com.mycompany.myapp.repository.FacturaRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacturaResourceIT {

    private static final ZonedDateTime DEFAULT_FECHA_FACTURA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_FACTURA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BigDecimal DEFAULT_MONTO_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTO_TOTAL = new BigDecimal(2);

    private static final MetodoPago DEFAULT_METODO_PAGO = MetodoPago.EFECTIVO;
    private static final MetodoPago UPDATED_METODO_PAGO = MetodoPago.QR;

    private static final String ENTITY_API_URL = "/api/facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacturaMockMvc;

    private Factura factura;

    private Factura insertedFactura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity() {
        return new Factura().fechaFactura(DEFAULT_FECHA_FACTURA).montoTotal(DEFAULT_MONTO_TOTAL).metodoPago(DEFAULT_METODO_PAGO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity() {
        return new Factura().fechaFactura(UPDATED_FECHA_FACTURA).montoTotal(UPDATED_MONTO_TOTAL).metodoPago(UPDATED_METODO_PAGO);
    }

    @BeforeEach
    void initTest() {
        factura = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFactura != null) {
            facturaRepository.delete(insertedFactura);
            insertedFactura = null;
        }
    }

    @Test
    @Transactional
    void createFactura() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Factura
        var returnedFactura = om.readValue(
            restFacturaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Factura.class
        );

        // Validate the Factura in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFacturaUpdatableFieldsEquals(returnedFactura, getPersistedFactura(returnedFactura));

        insertedFactura = returnedFactura;
    }

    @Test
    @Transactional
    void createFacturaWithExistingId() throws Exception {
        // Create the Factura with an existing ID
        factura.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFechaFacturaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setFechaFactura(null);

        // Create the Factura, which fails.

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontoTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setMontoTotal(null);

        // Create the Factura, which fails.

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMetodoPagoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setMetodoPago(null);

        // Create the Factura, which fails.

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFacturas() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        // Get all the facturaList
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaFactura").value(hasItem(sameInstant(DEFAULT_FECHA_FACTURA))))
            .andExpect(jsonPath("$.[*].montoTotal").value(hasItem(sameNumber(DEFAULT_MONTO_TOTAL))))
            .andExpect(jsonPath("$.[*].metodoPago").value(hasItem(DEFAULT_METODO_PAGO.toString())));
    }

    @Test
    @Transactional
    void getFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        // Get the factura
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL_ID, factura.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(factura.getId().intValue()))
            .andExpect(jsonPath("$.fechaFactura").value(sameInstant(DEFAULT_FECHA_FACTURA)))
            .andExpect(jsonPath("$.montoTotal").value(sameNumber(DEFAULT_MONTO_TOTAL)))
            .andExpect(jsonPath("$.metodoPago").value(DEFAULT_METODO_PAGO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFactura() throws Exception {
        // Get the factura
        restFacturaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFactura are not directly saved in db
        em.detach(updatedFactura);
        updatedFactura.fechaFactura(UPDATED_FECHA_FACTURA).montoTotal(UPDATED_MONTO_TOTAL).metodoPago(UPDATED_METODO_PAGO);

        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFactura.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFacturaToMatchAllProperties(updatedFactura);
    }

    @Test
    @Transactional
    void putNonExistingFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(put(ENTITY_API_URL_ID, factura.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(factura))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(factura)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.fechaFactura(UPDATED_FECHA_FACTURA).metodoPago(UPDATED_METODO_PAGO);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacturaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFactura, factura), getPersistedFactura(factura));
    }

    @Test
    @Transactional
    void fullUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.fechaFactura(UPDATED_FECHA_FACTURA).montoTotal(UPDATED_MONTO_TOTAL).metodoPago(UPDATED_METODO_PAGO);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacturaUpdatableFieldsEquals(partialUpdatedFactura, getPersistedFactura(partialUpdatedFactura));
    }

    @Test
    @Transactional
    void patchNonExistingFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, factura.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(factura))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(factura))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(factura)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.saveAndFlush(factura);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the factura
        restFacturaMockMvc
            .perform(delete(ENTITY_API_URL_ID, factura.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return facturaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Factura getPersistedFactura(Factura factura) {
        return facturaRepository.findById(factura.getId()).orElseThrow();
    }

    protected void assertPersistedFacturaToMatchAllProperties(Factura expectedFactura) {
        assertFacturaAllPropertiesEquals(expectedFactura, getPersistedFactura(expectedFactura));
    }

    protected void assertPersistedFacturaToMatchUpdatableProperties(Factura expectedFactura) {
        assertFacturaAllUpdatablePropertiesEquals(expectedFactura, getPersistedFactura(expectedFactura));
    }
}
