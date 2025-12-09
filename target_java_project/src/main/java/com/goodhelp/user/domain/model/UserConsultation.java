package com.goodhelp.user.domain.model;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.TherapistPrice;
import com.goodhelp.common.entity.BaseEntity;
import com.goodhelp.therapist.domain.model.Therapist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a consultation booked by a user with a therapist.
 * 
 * <p>A consultation links:</p>
 * <ul>
 *   <li>A user (client)</li>
 *   <li>A therapist</li>
 *   <li>A price option</li>
 *   <li>One or more schedule slots</li>
 * </ul>
 * 
 * <p>Business rules:</p>
 * <ul>
 *   <li>Consultations can be cancelled by user if 24+ hours before start time</li>
 *   <li>Consultations can be cancelled by therapist at any time</li>
 *   <li>When cancelled, schedule slots are released</li>
 *   <li>Only active (CREATED) consultations can be cancelled</li>
 * </ul>
 */
@Entity
@Table(name = "user_consultation", indexes = {
    @Index(name = "idx_user_consultation_user", columnList = "user_id"),
    @Index(name = "idx_user_consultation_psiholog", columnList = "psiholog_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class UserConsultation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psiholog_id", nullable = false)
    private Therapist therapist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psiholog_price_id")
    private TherapistPrice price;

    @Column(name = "state", nullable = false)
    @Convert(converter = ConsultationStateConverter.class)
    private ConsultationState state = ConsultationState.CREATED;

    @Column(name = "type", nullable = false)
    @Convert(converter = ConsultationTypeConverter.class)
    private ConsultationType type = ConsultationType.INDIVIDUAL;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserConsultationScheduleSlot> scheduleSlots = new ArrayList<>();

    /**
     * Private constructor - use factory methods.
     */
    private UserConsultation(User user, Therapist therapist, TherapistPrice price, ConsultationType type) {
        this.user = Objects.requireNonNull(user, "User is required");
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.price = price; // Can be null in some cases
        this.type = Objects.requireNonNull(type, "Consultation type is required");
        this.state = ConsultationState.CREATED;
    }

    /**
     * Create a new consultation.
     */
    public static UserConsultation create(User user, Therapist therapist, TherapistPrice price, ConsultationType type) {
        return new UserConsultation(user, therapist, price, type);
    }

    /**
     * Add a schedule slot to this consultation.
     */
    public void addScheduleSlot(ScheduleSlot slot) {
        Objects.requireNonNull(slot, "Schedule slot is required");
        UserConsultationScheduleSlot join = UserConsultationScheduleSlot.create(this, slot);
        this.scheduleSlots.add(join);
    }

    /**
     * Get the first (primary) schedule slot.
     * Most consultations have only one slot, but the schema allows multiple.
     */
    public ScheduleSlot getPrimarySlot() {
        return scheduleSlots.isEmpty() ? null : scheduleSlots.get(0).getScheduleSlot();
    }

    /**
     * Get the start time of the consultation (from primary slot).
     */
    public LocalDateTime getStartTime() {
        ScheduleSlot slot = getPrimarySlot();
        return slot != null ? slot.getAvailableAt() : null;
    }

    // ==================== Business Methods ====================

    /**
     * Check if this consultation can be cancelled by the user.
     * User can cancel if consultation is active and 24+ hours before start time.
     */
    public boolean canBeCancelledByUser(LocalDateTime now) {
        if (!state.isActive()) {
            return false;
        }
        LocalDateTime startTime = getStartTime();
        if (startTime == null) {
            return false;
        }
        LocalDateTime cancellationDeadline = startTime.minusHours(24);
        return now.isBefore(cancellationDeadline);
    }

    /**
     * Check if this consultation can be cancelled by the therapist.
     * Therapist can cancel at any time if consultation is active.
     */
    public boolean canBeCancelledByTherapist() {
        return state.isActive();
    }

    /**
     * Cancel this consultation by the user.
     * 
     * @param now current time
     * @throws IllegalStateException if consultation cannot be cancelled
     */
    public void cancelByUser(LocalDateTime now) {
        if (!canBeCancelledByUser(now)) {
            throw new IllegalStateException(
                "Consultation " + id + " cannot be cancelled by user: state=" + state + 
                ", startTime=" + getStartTime() + ", now=" + now
            );
        }
        
        boolean inTime = getStartTime().minusHours(24).isAfter(now);
        this.state = inTime ? 
            ConsultationState.CANCELLED_BY_USER_IN_TIME : 
            ConsultationState.CANCELLED_BY_USER_NOT_IN_TIME;
        
        // Release all schedule slots
        releaseScheduleSlots();
    }

    /**
     * Cancel this consultation by the therapist.
     * 
     * @param now current time
     * @throws IllegalStateException if consultation cannot be cancelled
     */
    public void cancelByTherapist(LocalDateTime now) {
        if (!canBeCancelledByTherapist()) {
            throw new IllegalStateException(
                "Consultation " + id + " cannot be cancelled by therapist: state=" + state
            );
        }
        
        LocalDateTime startTime = getStartTime();
        boolean inTime = startTime != null && startTime.minusHours(24).isAfter(now);
        this.state = inTime ? 
            ConsultationState.CANCELLED_BY_PSIHOLOG_IN_TIME : 
            ConsultationState.CANCELLED_BY_PSIHOLOG_NOT_IN_TIME;
        
        // Release all schedule slots
        releaseScheduleSlots();
    }

    /**
     * Mark consultation as completed.
     */
    public void markCompleted() {
        if (!state.isActive()) {
            throw new IllegalStateException(
                "Cannot mark non-active consultation as completed: " + id + ", state=" + state
            );
        }
        this.state = ConsultationState.COMPLETED;
    }

    /**
     * Check if this consultation is active (can be modified/cancelled).
     */
    public boolean isActive() {
        return state.isActive();
    }

    /**
     * Check if this consultation is completed.
     */
    public boolean isCompleted() {
        return state.isCompleted();
    }

    /**
     * Check if this consultation is cancelled.
     */
    public boolean isCancelled() {
        return state.isCancelled();
    }

    /**
     * Release all schedule slots associated with this consultation.
     */
    private void releaseScheduleSlots() {
        for (UserConsultationScheduleSlot join : scheduleSlots) {
            ScheduleSlot slot = join.getScheduleSlot();
            if (slot != null && slot.isBooked()) {
                slot.release();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConsultation that = (UserConsultation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserConsultation[id=%d, user=%d, therapist=%d, state=%s, type=%s]",
            id, user != null ? user.getId() : null, therapist != null ? therapist.getId() : null, state, type);
    }
}

