/**
 * User domain model - part of the Identity and Booking bounded contexts.
 * 
 * <p>This package contains the core domain model for users including:</p>
 * <ul>
 *   <li>{@link com.goodhelp.user.domain.model.User} - Aggregate root</li>
 *   <li>{@link com.goodhelp.user.domain.model.UserAutologinToken} - Authentication token entity</li>
 *   <li>{@link com.goodhelp.user.domain.model.UserConsultation} - Consultation entity</li>
 *   <li>{@link com.goodhelp.user.domain.model.UserConsultationScheduleSlot} - Join table entity</li>
 * </ul>
 * 
 * <p>Enums:</p>
 * <ul>
 *   <li>{@link com.goodhelp.user.domain.model.ConsultationState} - Consultation state (CREATED, COMPLETED, CANCELLED_*)</li>
 *   <li>{@link com.goodhelp.user.domain.model.ConsultationType} - Consultation type (INDIVIDUAL, COUPLE)</li>
 * </ul>
 */
package com.goodhelp.user.domain.model;

