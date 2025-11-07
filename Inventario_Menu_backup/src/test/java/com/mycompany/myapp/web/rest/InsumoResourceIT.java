package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InsumoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Insumo;
import com.mycompany.myapp.repository.InsumoRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link InsumoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InsumoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final Integer DEFAULT_STOCK_MINIMO = 1;
    private static final Integer UPDATED_STOCK_MINIMO = 2;

    private static final Integer DEFAULT_STOCK_ACTUAL = 1;
    private static final Integer UPDATED_STOCK_ACTUAL = 2;

    private static final String ENTITY_API_URL = "/api/insumos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InsumoRepository insumoRepository;

    @Autowired
    private MockMvc restInsumoMockMvc;

    private Insumo insumo;

    private Insumo insertedInsumo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Insumo createEntity() {
        return new Insumo().nombre(DEFAULT_NOMBRE).stockMinimo(DEFAULT_STOCK_MINIMO).stockActual(DEFAULT_STOCK_ACTUAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Insumo createUpdatedEntity() {
        return new Insumo().nombre(UPDATED_NOMBRE).stockMinimo(UPDATED_STOCK_MINIMO).stockActual(UPDATED_STOCK_ACTUAL);
    }

    @BeforeEach
    void initTest() {
        insumo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInsumo != null) {
            insumoRepository.delete(insertedInsumo);
            insertedInsumo = null;
        }
    }

    @Test
    void createInsumo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Insumo
        var returnedInsumo = om.readValue(
            restInsumoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(insumo)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Insumo.class
        );

        // Validate the Insumo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInsumoUpdatableFieldsEquals(returnedInsumo, getPersistedInsumo(returnedInsumo));

        insertedInsumo = returnedInsumo;
    }

    @Test
    void createInsumoWithExistingId() throws Exception {
        // Create the Insumo with an existing ID
        insumo.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInsumoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(insumo)))
            .andExpect(status().isBadRequest());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        insumo.setNombre(null);

        // Create the Insumo, which fails.

        restInsumoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(insumo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInsumos() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        // Get all the insumoList
        restInsumoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insumo.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].stockMinimo").value(hasItem(DEFAULT_STOCK_MINIMO)))
            .andExpect(jsonPath("$.[*].stockActual").value(hasItem(DEFAULT_STOCK_ACTUAL)));
    }

    @Test
    void getInsumo() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        // Get the insumo
        restInsumoMockMvc
            .perform(get(ENTITY_API_URL_ID, insumo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insumo.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.stockMinimo").value(DEFAULT_STOCK_MINIMO))
            .andExpect(jsonPath("$.stockActual").value(DEFAULT_STOCK_ACTUAL));
    }

    @Test
    void getNonExistingInsumo() throws Exception {
        // Get the insumo
        restInsumoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingInsumo() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the insumo
        Insumo updatedInsumo = insumoRepository.findById(insumo.getId()).orElseThrow();
        updatedInsumo.nombre(UPDATED_NOMBRE).stockMinimo(UPDATED_STOCK_MINIMO).stockActual(UPDATED_STOCK_ACTUAL);

        restInsumoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInsumo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInsumo))
            )
            .andExpect(status().isOk());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInsumoToMatchAllProperties(updatedInsumo);
    }

    @Test
    void putNonExistingInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(put(ENTITY_API_URL_ID, insumo.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(insumo)))
            .andExpect(status().isBadRequest());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(insumo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(insumo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInsumoWithPatch() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the insumo using partial update
        Insumo partialUpdatedInsumo = new Insumo();
        partialUpdatedInsumo.setId(insumo.getId());

        partialUpdatedInsumo.stockMinimo(UPDATED_STOCK_MINIMO).stockActual(UPDATED_STOCK_ACTUAL);

        restInsumoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsumo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInsumo))
            )
            .andExpect(status().isOk());

        // Validate the Insumo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInsumoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInsumo, insumo), getPersistedInsumo(insumo));
    }

    @Test
    void fullUpdateInsumoWithPatch() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the insumo using partial update
        Insumo partialUpdatedInsumo = new Insumo();
        partialUpdatedInsumo.setId(insumo.getId());

        partialUpdatedInsumo.nombre(UPDATED_NOMBRE).stockMinimo(UPDATED_STOCK_MINIMO).stockActual(UPDATED_STOCK_ACTUAL);

        restInsumoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsumo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInsumo))
            )
            .andExpect(status().isOk());

        // Validate the Insumo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInsumoUpdatableFieldsEquals(partialUpdatedInsumo, getPersistedInsumo(partialUpdatedInsumo));
    }

    @Test
    void patchNonExistingInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, insumo.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(insumo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(insumo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInsumo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        insumo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsumoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(insumo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Insumo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInsumo() throws Exception {
        // Initialize the database
        insertedInsumo = insumoRepository.save(insumo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the insumo
        restInsumoMockMvc
            .perform(delete(ENTITY_API_URL_ID, insumo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return insumoRepository.count();
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

    protected Insumo getPersistedInsumo(Insumo insumo) {
        return insumoRepository.findById(insumo.getId()).orElseThrow();
    }

    protected void assertPersistedInsumoToMatchAllProperties(Insumo expectedInsumo) {
        assertInsumoAllPropertiesEquals(expectedInsumo, getPersistedInsumo(expectedInsumo));
    }

    protected void assertPersistedInsumoToMatchUpdatableProperties(Insumo expectedInsumo) {
        assertInsumoAllUpdatablePropertiesEquals(expectedInsumo, getPersistedInsumo(expectedInsumo));
    }
}
