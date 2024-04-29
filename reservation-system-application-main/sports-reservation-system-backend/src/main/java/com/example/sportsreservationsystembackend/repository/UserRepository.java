package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This interface represents user repository
 * @author Radim Stejskal
 */

@Repository
public interface UserRepository extends JpaRepository<AppUser, String>, JpaSpecificationExecutor<AppUser> {

    boolean existsByEmail(String username);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByRole(UserRole role);
}
