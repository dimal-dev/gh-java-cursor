package com.goodhelp.landing.presentation.api;

import com.goodhelp.landing.application.usecase.ApplyPromocodeRequest;
import com.goodhelp.landing.application.usecase.ApplyPromocodeUseCase;
import com.goodhelp.landing.application.usecase.PromocodeResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API endpoints for booking-related AJAX calls.
 */
@RestController
@RequestMapping("/api/book-consultation")
public class BookConsultationApiController {

    private final ApplyPromocodeUseCase applyPromocodeUseCase;

    public BookConsultationApiController(ApplyPromocodeUseCase applyPromocodeUseCase) {
        this.applyPromocodeUseCase = applyPromocodeUseCase;
    }

    @PostMapping("/apply-promocode")
    public PromocodeResult applyPromocode(@Valid @RequestBody ApplyPromocodeRequest request) {
        return applyPromocodeUseCase.execute(request);
    }
}

