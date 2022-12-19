package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocationName(String name);

    List<Event> findByLocationId(Long id);
}
