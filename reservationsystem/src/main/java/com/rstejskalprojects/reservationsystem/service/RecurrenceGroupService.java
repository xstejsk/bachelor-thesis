package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;

import java.util.List;

public interface RecurrenceGroupService {

    List<RecurrenceGroup> findByFrequency(FrequencyEnum frequency);

    RecurrenceGroup saveRecurrenceGroup(RecurrenceGroup recurrenceGroup);
}
