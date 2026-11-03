package com.trackertreball.repository;

import com.trackertreball.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByUsernameIgnoreCase(String username);
    Optional<UserCredential> findByEmployeeId(Long employeeId);
}

