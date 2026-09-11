package com.location.identites.repository;

import com.location.identites.entity.UtilisateurEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<UtilisateurEntity, UUID> {
    Optional<UtilisateurEntity> findByEmail(String email);
    Optional<UtilisateurEntity> findByTelephone(String telephone);
}
