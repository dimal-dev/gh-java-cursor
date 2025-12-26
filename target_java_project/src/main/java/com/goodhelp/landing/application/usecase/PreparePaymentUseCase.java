package com.goodhelp.landing.application.usecase;

import com.goodhelp.billing.application.WayForPaySignatureService;
import com.goodhelp.billing.domain.model.Order;
import com.goodhelp.billing.domain.model.OrderSchedule;
import com.goodhelp.billing.domain.repository.OrderRepository;
import com.goodhelp.billing.domain.repository.OrderScheduleRepository;
import com.goodhelp.config.GoodHelpProperties;
import com.goodhelp.landing.application.dto.CheckoutSummaryDto;
import com.goodhelp.landing.application.dto.PaymentFormDataDto;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Prepares payment form data and persists an order for the checkout.
 */
@Service
@Transactional
public class PreparePaymentUseCase {

    private static final String WAYFORPAY_ACTION_URL = "https://secure.wayforpay.com/pay";

    private final GoodHelpProperties properties;
    private final WayForPaySignatureService signatureService;
    private final OrderRepository orderRepository;
    private final OrderScheduleRepository orderScheduleRepository;
    private final MessageSource messageSource;

    public PreparePaymentUseCase(GoodHelpProperties properties,
            WayForPaySignatureService signatureService,
            OrderRepository orderRepository,
            OrderScheduleRepository orderScheduleRepository,
            MessageSource messageSource) {
        this.properties = properties;
        this.signatureService = signatureService;
        this.orderRepository = orderRepository;
        this.orderScheduleRepository = orderScheduleRepository;
        this.messageSource = messageSource;
    }

    public PaymentFormDataDto execute(CheckoutSummaryDto checkout, PaymentContext context) {
        Locale locale = context.locale() != null ? context.locale() : Locale.ENGLISH;
        String orderSlug = UUID.randomUUID().toString().replace("-", "");

        int priceInCents = checkout.price().finalPrice() * 100;
        Order order = Order.create(
                orderSlug,
                priceInCents,
                checkout.price().currency(),
                checkout.therapistPriceId(),
                checkout.checkoutId(),
                null,
                checkout.client().phone(),
                checkout.client().email(),
                checkout.client().name(),
                checkout.session().timezoneId(),
                locale.getLanguage(),
                checkout.gaClientId(),
                context.requestCookies());

        Order savedOrder = orderRepository.save(order);
        if (checkout.scheduleSlotId() != null) {
            orderScheduleRepository.save(OrderSchedule.create(savedOrder.getId(), checkout.scheduleSlotId()));
        }

        BigDecimal amount = BigDecimal.valueOf(priceInCents, 2).setScale(2, RoundingMode.HALF_UP);
        String amountStr = amount.toPlainString();

        String productName = buildProductName(checkout, locale);
        String returnUrl = buildUrl(context.baseUrl(), context.returnPath());
        String serviceUrl = buildUrl(context.baseUrl(), context.servicePath());

        long orderDate = savedOrder.getCreatedAt()
                .atZone(ZoneOffset.UTC)
                .toEpochSecond();

        String signature = signatureService.sign(List.of(
                properties.getWayforpay().getMerchantLogin(),
                properties.getWayforpay().getMerchantDomain(),
                savedOrder.getCheckoutSlug(),
                String.valueOf(orderDate),
                amountStr,
                savedOrder.getCurrency(),
                productName,
                "1",
                amountStr));

        return new PaymentFormDataDto(
                WAYFORPAY_ACTION_URL,
                properties.getWayforpay().getMerchantLogin(),
                properties.getWayforpay().getMerchantDomain(),
                savedOrder.getCheckoutSlug(),
                orderDate,
                amountStr,
                savedOrder.getCurrency(),
                productName,
                amountStr,
                returnUrl,
                serviceUrl,
                signature);
    }

    private String buildProductName(CheckoutSummaryDto checkout, Locale locale) {
        String consultationLabel = messageSource.getMessage("Consultation", null, locale);
        return consultationLabel + ". " + checkout.therapist().fullName();
    }

    private String buildUrl(String baseUrl, String path) {
        String safeBase = StringUtils.hasText(baseUrl) ? baseUrl : "";
        String safePath = StringUtils.hasText(path) ? path : "";
        if (safePath.startsWith("/")) {
            safePath = safePath.substring(1);
        }
        if (!safeBase.endsWith("/")) {
            safeBase = safeBase + "/";
        }
        return safeBase + safePath;
    }
}
