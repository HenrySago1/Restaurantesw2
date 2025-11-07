package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cliente entity.
 */
@Repository
public interface ClienteRepository extends MongoRepository<Cliente, Long> {}
