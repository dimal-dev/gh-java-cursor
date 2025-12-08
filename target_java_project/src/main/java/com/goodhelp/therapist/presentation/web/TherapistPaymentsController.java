package com.goodhelp.therapist.presentation.web;

import com.goodhelp.therapist.application.dto.PaymentHistoryItemDto;
import com.goodhelp.therapist.application.dto.PaymentsSummaryDto;
import com.goodhelp.therapist.application.query.GetPaymentsHistoryQuery;
import com.goodhelp.therapist.application.usecase.GetPaymentHistoryUseCase;
import com.goodhelp.therapist.application.usecase.GetPaymentsSummaryUseCase;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for therapist payments and earnings history.
 * Handles current payout view and historical payouts.
 */
@Controller
@RequestMapping("/therapist/payments")
public class TherapistPaymentsController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final GetPaymentsSummaryUseCase getPaymentsSummaryUseCase;
    private final GetPaymentHistoryUseCase getPaymentHistoryUseCase;

    public TherapistPaymentsController(
            GetPaymentsSummaryUseCase getPaymentsSummaryUseCase,
            GetPaymentHistoryUseCase getPaymentHistoryUseCase) {
        this.getPaymentsSummaryUseCase = getPaymentsSummaryUseCase;
        this.getPaymentHistoryUseCase = getPaymentHistoryUseCase;
    }

    /**
     * Display payments overview (current payout period).
     */
    @GetMapping
    public String paymentsOverview(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        PaymentsSummaryDto summary = getPaymentsSummaryUseCase.execute(user.getId());
        
        model.addAttribute("summary", summary);
        model.addAttribute("currentUser", user);
        model.addAttribute("activeTab", "current");
        
        return "therapist/payments/index";
    }

    /**
     * Display payment history page.
     */
    @GetMapping("/history")
    public String paymentsHistory(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        model.addAttribute("currentUser", user);
        model.addAttribute("activeTab", "history");
        
        return "therapist/payments/history";
    }

    /**
     * Get payment history items (AJAX endpoint for KTDatatable).
     * Returns data in format compatible with KTDatatable.
     */
    @GetMapping("/history/items")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getHistoryItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perpage,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        GetPaymentsHistoryQuery query = new GetPaymentsHistoryQuery(
            user.getId(),
            page,
            perpage > 0 ? perpage : DEFAULT_PAGE_SIZE
        );
        
        Page<PaymentHistoryItemDto> historyPage = getPaymentHistoryUseCase.execute(query);
        
        // Convert to KTDatatable compatible format
        List<Map<String, Object>> items = historyPage.getContent().stream()
            .map(this::toKtDatatableFormat)
            .toList();
        
        return ResponseEntity.ok(items);
    }

    /**
     * Convert PaymentHistoryItemDto to KTDatatable format.
     * KTDatatable expects specific field names.
     */
    private Map<String, Object> toKtDatatableFormat(PaymentHistoryItemDto item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.consultationId());
        map.put("amount", item.earnings());
        map.put("date_created", item.consultationDate() != null 
            ? item.consultationDate().toString() 
            : "");
        return map;
    }
}

