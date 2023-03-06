package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordTokenRespository extends JpaRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE PasswordToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    void updateConfirmedAt(String token, LocalDateTime confirmedAt);
}

