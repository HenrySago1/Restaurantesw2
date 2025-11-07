package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PlatoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Plato;
import com.mycompany.myapp.repository.PlatoRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link PlatoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlatoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO = new BigDecimal(2);

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/platoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlatoRepository platoRepository;

    @Mock
    private PlatoRepository platoRepositoryMock;

    @Autowired
    private MockMvc restPlatoMockMvc;

    private Plato plato;

    private Plato insertedPlato;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plato createEntity() {
        return new Plato().nombre(DEFAULT_NOMBRE).descripcion(DEFAULT_DESCRIPCION).precio(DEFAULT_PRECIO).activo(DEFAULT_ACTIVO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plato createUpdatedEntity() {
        return new Plato().nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).precio(UPDATED_PRECIO).activo(UPDATED_ACTIVO);
    }

    @BeforeEach
    void initTest() {
        plato = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlato != null) {
            platoRepository.delete(insertedPlato);
            insertedPlato = null;
        }
    }

    @Test
    void createPlato() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Plato
        var returnedPlato = om.readValue(
            restPlatoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Plato.class
        );

        // Validate the Plato in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPlatoUpdatableFieldsEquals(returnedPlato, getPersistedPlato(returnedPlato));

        insertedPlato = returnedPlato;
    }

    @Test
    void createPlatoWithExistingId() throws Exception {
        // Create the Plato with an existing ID
        plato.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
            .andExpect(status().isBadRequest());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plato.setNombre(null);

        // Create the Plato, which fails.

        restPlatoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plato.setPrecio(null);

        // Create the Plato, which fails.

        restPlatoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPlatoes() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        // Get all the platoList
        restPlatoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plato.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(sameNumber(DEFAULT_PRECIO))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatoesWithEagerRelationshipsIsEnabled() throws Exception {
        when(platoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(platoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatoesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(platoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(platoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPlato() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        // Get the plato
        restPlatoMockMvc
            .perform(get(ENTITY_API_URL_ID, plato.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plato.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.precio").value(sameNumber(DEFAULT_PRECIO)))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    void getNonExistingPlato() throws Exception {
        // Get the plato
        restPlatoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPlato() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plato
        Plato updatedPlato = platoRepository.findById(plato.getId()).orElseThrow();
        updatedPlato.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).precio(UPDATED_PRECIO).activo(UPDATED_ACTIVO);

        restPlatoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlato.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPlato))
            )
            .andExpect(status().isOk());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlatoToMatchAllProperties(updatedPlato);
    }

    @Test
    void putNonExistingPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(put(ENTITY_API_URL_ID, plato.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
            .andExpect(status().isBadRequest());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(plato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(plato)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlatoWithPatch() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plato using partial update
        Plato partialUpdatedPlato = new Plato();
        partialUpdatedPlato.setId(plato.getId());

        partialUpdatedPlato.descripcion(UPDATED_DESCRIPCION).activo(UPDATED_ACTIVO);

        restPlatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlato.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlato))
            )
            .andExpect(status().isOk());

        // Validate the Plato in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlatoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlato, plato), getPersistedPlato(plato));
    }

    @Test
    void fullUpdatePlatoWithPatch() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plato using partial update
        Plato partialUpdatedPlato = new Plato();
        partialUpdatedPlato.setId(plato.getId());

        partialUpdatedPlato.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).precio(UPDATED_PRECIO).activo(UPDATED_ACTIVO);

        restPlatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlato.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlato))
            )
            .andExpect(status().isOk());

        // Validate the Plato in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlatoUpdatableFieldsEquals(partialUpdatedPlato, getPersistedPlato(partialUpdatedPlato));
    }

    @Test
    void patchNonExistingPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, plato.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(plato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(plato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlato() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plato.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(plato)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plato in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlato() throws Exception {
        // Initialize the database
        insertedPlato = platoRepository.save(plato);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the plato
        restPlatoMockMvc
            .perform(delete(ENTITY_API_URL_ID, plato.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return platoRepository.count();
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

    protected Plato getPersistedPlato(Plato plato) {
        return platoRepository.findById(plato.getId()).orElseThrow();
    }

    protected void assertPersistedPlatoToMatchAllProperties(Plato expectedPlato) {
        assertPlatoAllPropertiesEquals(expectedPlato, getPersistedPlato(expectedPlato));
    }

    protected void assertPersistedPlatoToMatchUpdatableProperties(Plato expectedPlato) {
        assertPlatoAllUpdatablePropertiesEquals(expectedPlato, getPersistedPlato(expectedPlato));
    }
}
