package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.dto.PaymentsSummaryDto;
import com.goodhelp.therapist.application.dto.PaymentsSummaryDto.PayoutItemDto;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case for retrieving payments summary for current payout period.
 * 
 * Payouts are scheduled:
 * - On the 15th of each month
 * - On the last day of each month
 */
@Service
@Transactional(readOnly = true)
public class GetPaymentsSummaryUseCase {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_CURRENCY = "UAH";

    private final TherapistRepository therapistRepository;

    public GetPaymentsSummaryUseCase(TherapistRepository therapistRepository) {
        this.therapistRepository = therapistRepository;
    }

    /**
     * Execute get payments summary query.
     *
     * @param therapistId the therapist ID
     * @return payments summary DTO
     */
    public PaymentsSummaryDto execute(Long therapistId) {
        // Verify therapist exists
        if (!therapistRepository.existsById(therapistId)) {
            return PaymentsSummaryDto.empty(DEFAULT_CURRENCY);
        }

        // Calculate current payout period
        LocalDate today = LocalDate.now();
        LocalDate periodStart = calculatePeriodStart(today);
        LocalDate periodEnd = calculatePeriodEnd(today);
        LocalDate nextPayoutDate = calculateNextPayoutDate(today);

        // TODO: When billing module is implemented, fetch actual completed consultations
        // For now, return sample/placeholder data matching PHP behavior
        List<PayoutItemDto> items = getSamplePayoutItems();
        BigDecimal totalAmount = items.stream()
            .map(PayoutItemDto::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentsSummaryDto(
            totalAmount,
            DEFAULT_CURRENCY,
            periodStart.format(DATE_FORMATTER),
            periodEnd.format(DATE_FORMATTER),
            nextPayoutDate.format(DATE_FORMATTER),
            items.size(),
            items
        );
    }

    /**
     * Calculate the start of the current payout period.
     * Period starts on the 1st or 16th of the month.
     */
    private LocalDate calculatePeriodStart(LocalDate today) {
        int dayOfMonth = today.getDayOfMonth();
        if (dayOfMonth <= 15) {
            return today.withDayOfMonth(1);
        } else {
            return today.withDayOfMonth(16);
        }
    }

    /**
     * Calculate the end of the current payout period.
     * Period ends on the 15th or last day of the month.
     */
    private LocalDate calculatePeriodEnd(LocalDate today) {
        int dayOfMonth = today.getDayOfMonth();
        if (dayOfMonth <= 15) {
            return today.withDayOfMonth(15);
        } else {
            return today.withDayOfMonth(today.lengthOfMonth());
        }
    }

    /**
     * Calculate the next payout date.
     * Payouts are on the 15th and last day of month, +2 days for processing.
     */
    private LocalDate calculateNextPayoutDate(LocalDate today) {
        int dayOfMonth = today.getDayOfMonth();
        if (dayOfMonth <= 15) {
            // Next payout after the 15th
            return today.withDayOfMonth(15).plusDays(2);
        } else {
            // Next payout after end of month
            return today.withDayOfMonth(today.lengthOfMonth()).plusDays(2);
        }
    }

    /**
     * Get sample payout items for demonstration.
     * Will be replaced with actual data when billing module is implemented.
     */
    private List<PayoutItemDto> getSamplePayoutItems() {
        // Return empty list - actual implementation will come from billing module
        return new ArrayList<>();
    }
}

