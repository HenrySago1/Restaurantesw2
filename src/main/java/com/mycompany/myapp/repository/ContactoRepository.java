package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Contacto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Contacto entity.
 */
@Repository
public interface ContactoRepository extends MongoRepository<Contacto, Long> {}
