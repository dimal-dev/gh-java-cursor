package com.goodhelp.user.domain.model;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;

/**
 * Join table entity linking UserConsultation to ScheduleSlot.
 * 
 * <p>This represents the many-to-many relationship between consultations
 * and schedule slots. In practice, most consultations have one slot,
 * but the schema allows multiple slots per consultation.</p>
 */
@Entity
@Table(name = "user_consultation_therapist_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class UserConsultationScheduleSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_consultation_id", nullable = false)
    private UserConsultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_schedule_id", nullable = false)
    private ScheduleSlot scheduleSlot;

    /**
     * Private constructor - use factory methods.
     */
    private UserConsultationScheduleSlot(UserConsultation consultation, ScheduleSlot scheduleSlot) {
        this.consultation = Objects.requireNonNull(consultation, "Consultation is required");
        this.scheduleSlot = Objects.requireNonNull(scheduleSlot, "Schedule slot is required");
    }

    /**
     * Create a new join entity linking a consultation to a schedule slot.
     */
    public static UserConsultationScheduleSlot create(UserConsultation consultation, ScheduleSlot scheduleSlot) {
        return new UserConsultationScheduleSlot(consultation, scheduleSlot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConsultationScheduleSlot that = (UserConsultationScheduleSlot) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserConsultationScheduleSlot[id=%d, consultation=%d, slot=%d]",
            id, consultation != null ? consultation.getId() : null, 
            scheduleSlot != null ? scheduleSlot.getId() : null);
    }
}

