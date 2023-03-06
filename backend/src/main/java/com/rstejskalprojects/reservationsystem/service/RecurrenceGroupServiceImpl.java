package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.repository.RecurrenceGroupRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.RecurrenceGroupNotFoundException;
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
    public RecurrenceGroup saveRecurrenceGroup(RecurrenceGroup recurrenceGroup) {
        log.info("saving recurrence group");
        return recurrenceGroupRepository.save(recurrenceGroup);
    }

    @Override
    public void deleteById(Long id) {
        recurrenceGroupRepository.findById(id).orElseThrow(() -> new RecurrenceGroupNotFoundException(String.format("recurrence group of id %s not found", id)));
        recurrenceGroupRepository.deleteById(id);
    }
}
