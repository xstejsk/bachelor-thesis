package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents recurrence group repository
 * @author Radim Stejskal
 */

@Repository
public interface RecurrenceGroupRepository extends JpaRepository<RecurrenceGroup, String> {
}
