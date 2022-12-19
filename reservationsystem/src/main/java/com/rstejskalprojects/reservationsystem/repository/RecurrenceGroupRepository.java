package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurrenceGroupRepository extends JpaRepository<RecurrenceGroup, Long> {

    List<RecurrenceGroup> findByFrequency(FrequencyEnum frequency);
}
