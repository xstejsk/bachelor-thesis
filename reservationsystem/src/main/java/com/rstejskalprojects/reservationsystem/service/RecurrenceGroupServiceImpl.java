package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.repository.RecurrenceGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecurrenceGroupServiceImpl implements RecurrenceGroupService {

    private final RecurrenceGroupRepository recurrenceGroupRepository;

    @Override
    public List<RecurrenceGroup> findByFrequency(FrequencyEnum frequency) {
        return recurrenceGroupRepository.findByFrequency(frequency);
    }

    @Override
    public RecurrenceGroup saveRecurrenceGroup(RecurrenceGroup recurrenceGroup) {
        return recurrenceGroupRepository.save(recurrenceGroup);
    }
}
