package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Categoria entity.
 */
@Repository
public interface CategoriaRepository extends MongoRepository<Categoria, String> {}
