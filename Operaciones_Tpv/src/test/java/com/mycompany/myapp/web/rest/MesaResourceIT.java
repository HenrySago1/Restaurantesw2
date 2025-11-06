package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MesaAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Mesa;
import com.mycompany.myapp.domain.enumeration.EstadoMesa;
import com.mycompany.myapp.repository.MesaRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link MesaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MesaResourceIT {

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;

    private static final Integer DEFAULT_CAPACIDAD = 1;
    private static final Integer UPDATED_CAPACIDAD = 2;

    private static final EstadoMesa DEFAULT_ESTADO = EstadoMesa.LIBRE;
    private static final EstadoMesa UPDATED_ESTADO = EstadoMesa.OCUPADA;

    private static final String ENTITY_API_URL = "/api/mesas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMesaMockMvc;

    private Mesa mesa;

    private Mesa insertedMesa;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mesa createEntity() {
        return new Mesa().numero(DEFAULT_NUMERO).capacidad(DEFAULT_CAPACIDAD).estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mesa createUpdatedEntity() {
        return new Mesa().numero(UPDATED_NUMERO).capacidad(UPDATED_CAPACIDAD).estado(UPDATED_ESTADO);
    }

    @BeforeEach
    void initTest() {
        mesa = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMesa != null) {
            mesaRepository.delete(insertedMesa);
            insertedMesa = null;
        }
    }

    @Test
    @Transactional
    void createMesa() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Mesa
        var returnedMesa = om.readValue(
            restMesaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Mesa.class
        );

        // Validate the Mesa in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMesaUpdatableFieldsEquals(returnedMesa, getPersistedMesa(returnedMesa));

        insertedMesa = returnedMesa;
    }

    @Test
    @Transactional
    void createMesaWithExistingId() throws Exception {
        // Create the Mesa with an existing ID
        mesa.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMesaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mesa.setNumero(null);

        // Create the Mesa, which fails.

        restMesaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mesa.setCapacidad(null);

        // Create the Mesa, which fails.

        restMesaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mesa.setEstado(null);

        // Create the Mesa, which fails.

        restMesaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMesas() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        // Get all the mesaList
        restMesaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mesa.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].capacidad").value(hasItem(DEFAULT_CAPACIDAD)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @Test
    @Transactional
    void getMesa() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        // Get the mesa
        restMesaMockMvc
            .perform(get(ENTITY_API_URL_ID, mesa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mesa.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.capacidad").value(DEFAULT_CAPACIDAD))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMesa() throws Exception {
        // Get the mesa
        restMesaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMesa() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mesa
        Mesa updatedMesa = mesaRepository.findById(mesa.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMesa are not directly saved in db
        em.detach(updatedMesa);
        updatedMesa.numero(UPDATED_NUMERO).capacidad(UPDATED_CAPACIDAD).estado(UPDATED_ESTADO);

        restMesaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMesa.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMesa))
            )
            .andExpect(status().isOk());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMesaToMatchAllProperties(updatedMesa);
    }

    @Test
    @Transactional
    void putNonExistingMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(put(ENTITY_API_URL_ID, mesa.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mesa))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMesaWithPatch() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mesa using partial update
        Mesa partialUpdatedMesa = new Mesa();
        partialUpdatedMesa.setId(mesa.getId());

        partialUpdatedMesa.numero(UPDATED_NUMERO);

        restMesaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMesa.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMesa))
            )
            .andExpect(status().isOk());

        // Validate the Mesa in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMesaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMesa, mesa), getPersistedMesa(mesa));
    }

    @Test
    @Transactional
    void fullUpdateMesaWithPatch() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mesa using partial update
        Mesa partialUpdatedMesa = new Mesa();
        partialUpdatedMesa.setId(mesa.getId());

        partialUpdatedMesa.numero(UPDATED_NUMERO).capacidad(UPDATED_CAPACIDAD).estado(UPDATED_ESTADO);

        restMesaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMesa.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMesa))
            )
            .andExpect(status().isOk());

        // Validate the Mesa in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMesaUpdatableFieldsEquals(partialUpdatedMesa, getPersistedMesa(partialUpdatedMesa));
    }

    @Test
    @Transactional
    void patchNonExistingMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(patch(ENTITY_API_URL_ID, mesa.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isBadRequest());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mesa))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMesa() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mesa.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMesaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mesa)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mesa in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMesa() throws Exception {
        // Initialize the database
        insertedMesa = mesaRepository.saveAndFlush(mesa);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mesa
        restMesaMockMvc
            .perform(delete(ENTITY_API_URL_ID, mesa.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mesaRepository.count();
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

    protected Mesa getPersistedMesa(Mesa mesa) {
        return mesaRepository.findById(mesa.getId()).orElseThrow();
    }

    protected void assertPersistedMesaToMatchAllProperties(Mesa expectedMesa) {
        assertMesaAllPropertiesEquals(expectedMesa, getPersistedMesa(expectedMesa));
    }

    protected void assertPersistedMesaToMatchUpdatableProperties(Mesa expectedMesa) {
        assertMesaAllUpdatablePropertiesEquals(expectedMesa, getPersistedMesa(expectedMesa));
    }
}
