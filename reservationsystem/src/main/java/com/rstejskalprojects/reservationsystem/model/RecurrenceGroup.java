package com.rstejskalprojects.reservationsystem.model;

import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
public class RecurrenceRule {
    private FrequencyEnum frequency;
    private DayOfWeek[] daysOfWeek;
    private Integer interval;
    private Integer count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurrenceRule that = (RecurrenceRule) o;
        return frequency == that.frequency && Objects.equals(interval, that.interval) && Objects.equals(count, that.count);
        DayOfWeek.
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency, interval, count);
    }

    @Override
    public String toString() {
        return String.format("FREQ=%s;INTERVAL=%s;COUNT=%s", frequency.getValue(), interval, count);
    }
}
