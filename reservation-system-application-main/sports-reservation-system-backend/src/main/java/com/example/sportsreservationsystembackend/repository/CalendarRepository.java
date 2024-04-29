package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This interface represents calendar repository
 * @author Radim Stejskal
 */

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, String>, JpaSpecificationExecutor<Calendar> {

    boolean existsByName(String name);

}
