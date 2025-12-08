package com.goodhelp.booking.infrastructure.persistence;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain ScheduleSlotRepository interface
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleSlotRepositoryAdapter implements ScheduleSlotRepository {

    private final JpaScheduleSlotRepository jpaRepository;

    @Override
    public Optional<ScheduleSlot> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ScheduleSlot> findByTherapistIdAndAvailableAtBetween(
            Long therapistId, 
            LocalDateTime from, 
            LocalDateTime to) {
        return jpaRepository.findByTherapistIdAndAvailableAtBetween(therapistId, from, to);
    }

    @Override
    public List<ScheduleSlot> findByTherapistIdAndAvailableAtAfterAndStatus(
            Long therapistId, 
            LocalDateTime after, 
            SlotStatus status) {
        return jpaRepository.findByTherapistIdAndAvailableAtAfterAndStatus(therapistId, after, status);
    }

    @Override
    public List<ScheduleSlot> findByTherapistIdAndAvailableAtAfter(Long therapistId, LocalDateTime after) {
        return jpaRepository.findByTherapistIdAndAvailableAtAfter(therapistId, after);
    }

    @Override
    public Optional<ScheduleSlot> findByTherapistIdAndAvailableAt(Long therapistId, LocalDateTime availableAt) {
        return jpaRepository.findByTherapistIdAndAvailableAt(therapistId, availableAt);
    }

    @Override
    @Transactional
    public ScheduleSlot save(ScheduleSlot slot) {
        return jpaRepository.save(slot);
    }

    @Override
    @Transactional
    public List<ScheduleSlot> saveAll(List<ScheduleSlot> slots) {
        return jpaRepository.saveAll(slots);
    }

    @Override
    @Transactional
    public void delete(ScheduleSlot slot) {
        jpaRepository.delete(slot);
    }

    @Override
    public long countByTherapistIdAndAvailableAtAfterAndStatus(
            Long therapistId, 
            LocalDateTime after, 
            SlotStatus status) {
        return jpaRepository.countByTherapistIdAndAvailableAtAfterAndStatus(therapistId, after, status);
    }
}

