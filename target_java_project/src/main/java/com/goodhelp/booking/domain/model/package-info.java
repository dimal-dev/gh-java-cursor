/**
 * Booking domain model - part of the Booking bounded context.
 * 
 * <p>This package contains the domain model for scheduling and pricing:</p>
 * <ul>
 *   <li>{@link com.goodhelp.booking.domain.model.ScheduleSlot} - Time slot entity</li>
 *   <li>{@link com.goodhelp.booking.domain.model.TherapistPrice} - Pricing entity</li>
 * </ul>
 * 
 * <p>Enums:</p>
 * <ul>
 *   <li>{@link com.goodhelp.booking.domain.model.SlotStatus} - Slot states (AVAILABLE, BOOKED, etc.)</li>
 *   <li>{@link com.goodhelp.booking.domain.model.PriceType} - Consultation types (INDIVIDUAL, COUPLE)</li>
 *   <li>{@link com.goodhelp.booking.domain.model.PriceState} - Price visibility (CURRENT, PAST, UNLISTED)</li>
 * </ul>
 * 
 * <p>This context handles:</p>
 * <ul>
 *   <li>Schedule slot management</li>
 *   <li>Booking lifecycle</li>
 *   <li>Pricing configuration</li>
 * </ul>
 */
package com.goodhelp.booking.domain.model;
