package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reserva entity.
 */
@Repository
public interface ReservaRepository extends MongoRepository<Reserva, Long> {}
