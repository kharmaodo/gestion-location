package com.location.identites.repository;

import com.location.identites.entity.ResetPasswordEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRepository extends JpaRepository<ResetPasswordEntity, UUID> {
    Optional<ResetPasswordEntity> findByTokenHashAndUtiliseFalse(String tokenHash);
}
