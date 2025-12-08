package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;

/**
 * Entity representing notes a therapist keeps about a specific user/client.
 * 
 * <p>These are private notes visible only to the therapist, used for:</p>
 * <ul>
 *   <li>Session notes and observations</li>
 *   <li>Treatment progress tracking</li>
 *   <li>Personal reminders about the client</li>
 * </ul>
 */
@Entity
@Table(name = "psiholog_user_notes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"psiholog_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TherapistUserNotes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psiholog_id", nullable = false)
    private Therapist therapist;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name")
    private String clientName;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Private constructor - use factory methods.
     */
    private TherapistUserNotes(Therapist therapist, Long userId, String clientName) {
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.clientName = clientName;
        this.notes = "";
    }

    /**
     * Create new notes for a client.
     */
    public static TherapistUserNotes create(Therapist therapist, Long userId, String clientName) {
        return new TherapistUserNotes(therapist, userId, clientName);
    }

    // ==================== Business Methods ====================

    /**
     * Update the notes content.
     */
    public void updateNotes(String newNotes) {
        this.notes = newNotes != null ? newNotes : "";
    }

    /**
     * Append content to existing notes.
     */
    public void appendNotes(String additionalNotes) {
        if (additionalNotes == null || additionalNotes.isBlank()) {
            return;
        }
        if (notes == null || notes.isBlank()) {
            this.notes = additionalNotes;
        } else {
            this.notes = notes + "\n\n" + additionalNotes;
        }
    }

    /**
     * Update the client's display name.
     */
    public void updateClientName(String newName) {
        this.clientName = newName;
    }

    /**
     * Check if notes are empty.
     */
    public boolean isEmpty() {
        return notes == null || notes.isBlank();
    }

    /**
     * Get notes or empty string (never null).
     */
    public String getNotesOrEmpty() {
        return notes != null ? notes : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistUserNotes that = (TherapistUserNotes) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
