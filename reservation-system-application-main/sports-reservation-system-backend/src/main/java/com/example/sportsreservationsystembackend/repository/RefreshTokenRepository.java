package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface represents refresh token repository
 * @author Radim Stejskal
 */

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
}
