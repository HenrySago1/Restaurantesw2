package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Insumo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Insumo entity.
 */
@Repository
public interface InsumoRepository extends MongoRepository<Insumo, String> {}
