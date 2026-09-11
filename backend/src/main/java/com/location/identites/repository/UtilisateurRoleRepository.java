package com.location.identites.repository;

import com.location.identites.entity.UtilisateurRoleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRoleEntity, UUID> {
    List<UtilisateurRoleEntity> findByUtilisateurId(UUID utilisateurId);
}
