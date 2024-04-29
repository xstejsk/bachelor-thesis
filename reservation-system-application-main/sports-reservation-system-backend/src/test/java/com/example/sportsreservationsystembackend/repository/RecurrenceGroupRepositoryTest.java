package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RecurrenceGroupRepositoryTest {

    @Autowired
    private RecurrenceGroupRepository recurrenceGroupRepository;

    @Test
    public void saveGroup() {
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        recurrenceGroup.setRepeatUntil(LocalDate.now().plusDays(4));
        recurrenceGroupRepository.save(recurrenceGroup);
        Assertions.assertEquals(1, recurrenceGroupRepository.findAll().size());

    }
}
