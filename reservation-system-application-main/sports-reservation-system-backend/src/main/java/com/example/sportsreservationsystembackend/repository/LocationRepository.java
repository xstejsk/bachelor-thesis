package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents location repository
 * @author Radim Stejskal
 */

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
}
