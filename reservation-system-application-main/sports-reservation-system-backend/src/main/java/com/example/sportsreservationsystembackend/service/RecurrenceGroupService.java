package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.RecurrenceGroup;

public interface RecurrenceGroupService {

    RecurrenceGroup save(RecurrenceGroup recurrenceGroup);

    RecurrenceGroup delete(String recurrenceGroupId);
}
