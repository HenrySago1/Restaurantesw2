package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Plato;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Plato entity.
 */
@Repository
public interface PlatoRepository extends MongoRepository<Plato, String> {
    @Query("{}")
    Page<Plato> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Plato> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Plato> findOneWithEagerRelationships(String id);
}
