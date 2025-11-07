package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ContactoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Contacto;
import com.mycompany.myapp.repository.ContactoRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ContactoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactoResourceIT {

    private static final ZonedDateTime DEFAULT_FECHA_CONTACTO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_CONTACTO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_MOTIVO = "AAAAAAAAAA";
    private static final String UPDATED_MOTIVO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contactos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactoMockMvc;

    private Contacto contacto;

    private Contacto insertedContacto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contacto createEntity() {
        return new Contacto().fechaContacto(DEFAULT_FECHA_CONTACTO).motivo(DEFAULT_MOTIVO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contacto createUpdatedEntity() {
        return new Contacto().fechaContacto(UPDATED_FECHA_CONTACTO).motivo(UPDATED_MOTIVO);
    }

    @BeforeEach
    void initTest() {
        contacto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContacto != null) {
            contactoRepository.delete(insertedContacto);
            insertedContacto = null;
        }
    }

    @Test
    @Transactional
    void createContacto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Contacto
        var returnedContacto = om.readValue(
            restContactoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contacto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Contacto.class
        );

        // Validate the Contacto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertContactoUpdatableFieldsEquals(returnedContacto, getPersistedContacto(returnedContacto));

        insertedContacto = returnedContacto;
    }

    @Test
    @Transactional
    void createContactoWithExistingId() throws Exception {
        // Create the Contacto with an existing ID
        contacto.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contacto)))
            .andExpect(status().isBadRequest());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMotivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contacto.setMotivo(null);

        // Create the Contacto, which fails.

        restContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contacto)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContactos() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        // Get all the contactoList
        restContactoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contacto.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaContacto").value(hasItem(sameInstant(DEFAULT_FECHA_CONTACTO))))
            .andExpect(jsonPath("$.[*].motivo").value(hasItem(DEFAULT_MOTIVO)));
    }

    @Test
    @Transactional
    void getContacto() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        // Get the contacto
        restContactoMockMvc
            .perform(get(ENTITY_API_URL_ID, contacto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contacto.getId().intValue()))
            .andExpect(jsonPath("$.fechaContacto").value(sameInstant(DEFAULT_FECHA_CONTACTO)))
            .andExpect(jsonPath("$.motivo").value(DEFAULT_MOTIVO));
    }

    @Test
    @Transactional
    void getNonExistingContacto() throws Exception {
        // Get the contacto
        restContactoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContacto() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contacto
        Contacto updatedContacto = contactoRepository.findById(contacto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContacto are not directly saved in db
        em.detach(updatedContacto);
        updatedContacto.fechaContacto(UPDATED_FECHA_CONTACTO).motivo(UPDATED_MOTIVO);

        restContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContacto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedContacto))
            )
            .andExpect(status().isOk());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContactoToMatchAllProperties(updatedContacto);
    }

    @Test
    @Transactional
    void putNonExistingContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contacto.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contacto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contacto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contacto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactoWithPatch() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contacto using partial update
        Contacto partialUpdatedContacto = new Contacto();
        partialUpdatedContacto.setId(contacto.getId());

        partialUpdatedContacto.fechaContacto(UPDATED_FECHA_CONTACTO);

        restContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContacto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContacto))
            )
            .andExpect(status().isOk());

        // Validate the Contacto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedContacto, contacto), getPersistedContacto(contacto));
    }

    @Test
    @Transactional
    void fullUpdateContactoWithPatch() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contacto using partial update
        Contacto partialUpdatedContacto = new Contacto();
        partialUpdatedContacto.setId(contacto.getId());

        partialUpdatedContacto.fechaContacto(UPDATED_FECHA_CONTACTO).motivo(UPDATED_MOTIVO);

        restContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContacto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContacto))
            )
            .andExpect(status().isOk());

        // Validate the Contacto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactoUpdatableFieldsEquals(partialUpdatedContacto, getPersistedContacto(partialUpdatedContacto));
    }

    @Test
    @Transactional
    void patchNonExistingContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contacto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contacto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contacto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contacto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contacto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContacto() throws Exception {
        // Initialize the database
        insertedContacto = contactoRepository.saveAndFlush(contacto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contacto
        restContactoMockMvc
            .perform(delete(ENTITY_API_URL_ID, contacto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contactoRepository.count();
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

    protected Contacto getPersistedContacto(Contacto contacto) {
        return contactoRepository.findById(contacto.getId()).orElseThrow();
    }

    protected void assertPersistedContactoToMatchAllProperties(Contacto expectedContacto) {
        assertContactoAllPropertiesEquals(expectedContacto, getPersistedContacto(expectedContacto));
    }

    protected void assertPersistedContactoToMatchUpdatableProperties(Contacto expectedContacto) {
        assertContactoAllUpdatablePropertiesEquals(expectedContacto, getPersistedContacto(expectedContacto));
    }
}
