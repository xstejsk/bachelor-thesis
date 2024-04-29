package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.RecurrenceGroupHasEventsException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.repository.RecurrenceGroupRepository;
import com.example.sportsreservationsystembackend.service.RecurrenceGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This class represents recurrence group service implementation
 *
 * @Author Radim Stejskal
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurrenceGroupServiceImpl implements RecurrenceGroupService {

    private final RecurrenceGroupRepository recurrenceGroupRepository;

    /**
     * This method saves recurrence group
     * @param recurrenceGroup to be saved
     * @return saved recurrence group
     */
    @Override
    public RecurrenceGroup save(RecurrenceGroup recurrenceGroup) {
        return recurrenceGroupRepository.save(recurrenceGroup);
    }

    /**
     * This method deletes recurrence group by id
     * @param recurrenceGroupId
     * @return deleted recurrence group
     */

    @Override
    public RecurrenceGroup delete(String recurrenceGroupId) {
        if (recurrenceGroupRepository.findById(recurrenceGroupId).isEmpty()) {
            throw new ResourceNotFoundException("Recurrence group with id " + recurrenceGroupId + " does not exist");
        }
        RecurrenceGroup recurrenceGroup = recurrenceGroupRepository.findById(recurrenceGroupId).get();
        if (!recurrenceGroup.getEvents().isEmpty()) {
            throw new RecurrenceGroupHasEventsException("Recurrence group with id " + recurrenceGroupId + " has events");
        }
        recurrenceGroupRepository.delete(recurrenceGroup);
        log.info("IN delete - recurrence group with id {} successfully deleted", recurrenceGroupId);
        return recurrenceGroup;
    }
}
