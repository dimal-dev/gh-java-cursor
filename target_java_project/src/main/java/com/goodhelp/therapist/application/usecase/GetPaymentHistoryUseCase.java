package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.dto.PaymentHistoryItemDto;
import com.goodhelp.therapist.application.query.GetPaymentsHistoryQuery;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case for retrieving payment history with pagination.
 * Returns completed payouts for the therapist.
 */
@Service
@Transactional(readOnly = true)
public class GetPaymentHistoryUseCase {

    private static final String DEFAULT_CURRENCY = "UAH";

    private final TherapistRepository therapistRepository;

    public GetPaymentHistoryUseCase(TherapistRepository therapistRepository) {
        this.therapistRepository = therapistRepository;
    }

    /**
     * Execute get payment history query.
     *
     * @param query the query with therapist ID and pagination
     * @return paginated payment history
     */
    public Page<PaymentHistoryItemDto> execute(GetPaymentsHistoryQuery query) {
        // Verify therapist exists
        if (!therapistRepository.existsById(query.therapistId())) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(query.page(), query.size());

        // TODO: When billing module is implemented, fetch actual payment history
        // For now, return sample data matching PHP behavior
        List<PaymentHistoryItemDto> allItems = getSamplePaymentHistory();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allItems.size());
        
        List<PaymentHistoryItemDto> pageContent = start < allItems.size() 
            ? allItems.subList(start, end) 
            : List.of();

        return new PageImpl<>(pageContent, pageable, allItems.size());
    }

    /**
     * Get sample payment history for demonstration.
     * Will be replaced with actual data when billing module is implemented.
     */
    private List<PaymentHistoryItemDto> getSamplePaymentHistory() {
        List<PaymentHistoryItemDto> items = new ArrayList<>();
        
        // Sample historical payouts matching PHP response
        items.add(new PaymentHistoryItemDto(
            2L,
            LocalDateTime.now().minusMonths(1),
            "Sample Client 1",
            "individual",
            5000,
            DEFAULT_CURRENCY,
            4000,  // earnings after platform fee
            "completed"
        ));
        
        items.add(new PaymentHistoryItemDto(
            1L,
            LocalDateTime.now().minusYears(1),
            "Sample Client 2",
            "individual",
            7000,
            DEFAULT_CURRENCY,
            5600,  // earnings after platform fee
            "completed"
        ));
        
        return items;
    }
}
