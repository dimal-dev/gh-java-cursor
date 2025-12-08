package com.goodhelp.therapist.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Value Object representing a therapist's profile information.
 * Embedded within the Therapist aggregate.
 * 
 * Contains biographical and display information used on the landing page
 * and in therapist listings.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class TherapistProfile {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "works_from")
    private LocalDate worksFrom;

    @Column(name = "profile_template")
    private String profileTemplate;

    @Column(name = "sex")
    @Convert(converter = SexConverter.class)
    private Sex sex;

    /**
     * Private constructor - use factory methods.
     */
    private TherapistProfile(String firstName, String lastName, LocalDate birthDate,
                             LocalDate worksFrom, String profileTemplate, Sex sex) {
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        this.birthDate = birthDate;
        this.worksFrom = worksFrom;
        this.profileTemplate = profileTemplate;
        this.sex = sex;
    }

    /**
     * Create a new profile with all fields.
     */
    public static TherapistProfile create(String firstName, String lastName, 
                                          LocalDate birthDate, LocalDate worksFrom,
                                          String profileTemplate, Sex sex) {
        return new TherapistProfile(firstName, lastName, birthDate, worksFrom, profileTemplate, sex);
    }

    /**
     * Create a minimal profile (for new therapists).
     */
    public static TherapistProfile minimal(String firstName, String lastName, Sex sex) {
        return new TherapistProfile(firstName, lastName, null, null, null, sex);
    }

    /**
     * Get full name for display.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate age from birth date.
     * 
     * @return age in years, or null if birth date is not set
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calculate years of experience from worksFrom date.
     * 
     * @return years of experience, or null if worksFrom is not set
     */
    public Integer getYearsOfExperience() {
        if (worksFrom == null) {
            return null;
        }
        return Period.between(worksFrom, LocalDate.now()).getYears();
    }

    /**
     * Check if this is a female therapist (for proper pronoun usage in UI).
     */
    public boolean isFemale() {
        return sex == Sex.FEMALE;
    }

    /**
     * Check if this is a male therapist.
     */
    public boolean isMale() {
        return sex == Sex.MALE;
    }

    /**
     * Create an updated profile with new first name.
     */
    public TherapistProfile withFirstName(String newFirstName) {
        return new TherapistProfile(newFirstName, lastName, birthDate, worksFrom, profileTemplate, sex);
    }

    /**
     * Create an updated profile with new last name.
     */
    public TherapistProfile withLastName(String newLastName) {
        return new TherapistProfile(firstName, newLastName, birthDate, worksFrom, profileTemplate, sex);
    }

    /**
     * Create an updated profile with new template.
     */
    public TherapistProfile withProfileTemplate(String newTemplate) {
        return new TherapistProfile(firstName, lastName, birthDate, worksFrom, newTemplate, sex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistProfile that = (TherapistProfile) o;
        return Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(birthDate, that.birthDate) &&
               Objects.equals(worksFrom, that.worksFrom) &&
               Objects.equals(profileTemplate, that.profileTemplate) &&
               sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate, worksFrom, profileTemplate, sex);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}

