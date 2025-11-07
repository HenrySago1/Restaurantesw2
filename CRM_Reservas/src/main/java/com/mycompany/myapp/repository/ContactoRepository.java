package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Contacto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Contacto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {}
