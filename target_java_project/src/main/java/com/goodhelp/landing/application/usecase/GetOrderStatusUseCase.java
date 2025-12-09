package com.goodhelp.landing.application.usecase;

import com.goodhelp.billing.domain.model.Order;
import com.goodhelp.billing.domain.model.OrderSchedule;
import com.goodhelp.billing.domain.model.OrderState;
import com.goodhelp.billing.domain.repository.OrderRepository;
import com.goodhelp.billing.domain.repository.OrderScheduleRepository;
import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.DateLocalizedHelper;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.OrderStatusDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Use case to get order status for thank you page.
 */
@Service
public class GetOrderStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetOrderStatusUseCase.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final OrderRepository orderRepository;
    private final OrderScheduleRepository orderScheduleRepository;
    private final TherapistPriceRepository therapistPriceRepository;
    private final TherapistRepository therapistRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final DateLocalizedHelper dateLocalizedHelper;
    private final TimezoneHelper timezoneHelper;

    public GetOrderStatusUseCase(OrderRepository orderRepository,
                                 OrderScheduleRepository orderScheduleRepository,
                                 TherapistPriceRepository therapistPriceRepository,
                                 TherapistRepository therapistRepository,
                                 ScheduleSlotRepository scheduleSlotRepository,
                                 DateLocalizedHelper dateLocalizedHelper,
                                 TimezoneHelper timezoneHelper) {
        this.orderRepository = orderRepository;
        this.orderScheduleRepository = orderScheduleRepository;
        this.therapistPriceRepository = therapistPriceRepository;
        this.therapistRepository = therapistRepository;
        this.scheduleSlotRepository = scheduleSlotRepository;
        this.dateLocalizedHelper = dateLocalizedHelper;
        this.timezoneHelper = timezoneHelper;
    }

    @Transactional(readOnly = true)
    public OrderStatusDto execute(String checkoutSlug, String userTimezone, String locale) {
        Order order = orderRepository.findByCheckoutSlug(checkoutSlug)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found for checkout slug: " + checkoutSlug));

        String status = mapOrderStateToStatus(order.getState());
        String therapistName = resolveTherapistName(order);
        String consultationDate = null;
        String consultationTime = null;
        String errorMessage = null;
        String loginUrl = null;

        // Get schedule information if order has been linked to a slot
        OrderSchedule orderSchedule = orderScheduleRepository.findByOrderId(order.getId()).orElse(null);
        if (orderSchedule != null) {
            ScheduleSlot slot = scheduleSlotRepository.findById(orderSchedule.getScheduleSlotId())
                .orElse(null);
            if (slot != null) {
                var dateTimeInfo = formatConsultationDateTime(slot, userTimezone, order.getTimezone(), locale);
                consultationDate = dateTimeInfo.date();
                consultationTime = dateTimeInfo.time();
            }
        }

        if (order.getState() == OrderState.FAILED) {
            errorMessage = "Payment processing failed";
        }

        // TODO: Generate login URL when user module is implemented
        // For now, loginUrl will be null

        return new OrderStatusDto(
            status,
            therapistName,
            consultationDate,
            consultationTime,
            errorMessage,
            loginUrl,
            order.getPrice()
        );
    }

    private String mapOrderStateToStatus(OrderState state) {
        return switch (state) {
            case CREATED, PENDING -> "pending";
            case APPROVED -> "approved";
            case FAILED -> "failed";
        };
    }

    private String resolveTherapistName(Order order) {
        if (order.getTherapistPriceId() == null) {
            return "";
        }

        try {
            TherapistPrice price = therapistPriceRepository.findById(order.getTherapistPriceId())
                .orElse(null);
            if (price == null) {
                return "";
            }

            Therapist therapist = therapistRepository.findById(price.getTherapistId())
                .orElse(null);
            if (therapist == null || therapist.getProfile() == null) {
                return "";
            }

            return therapist.getProfile().getFullName();
        } catch (Exception e) {
            log.warn("Failed to resolve therapist name for order {}", order.getId(), e);
            return "";
        }
    }

    private DateTimeInfo formatConsultationDateTime(ScheduleSlot slot,
                                                    String userTimezone,
                                                    String orderTimezone,
                                                    String localeCode) {
        String effectiveTimezone = resolveTimezone(userTimezone, orderTimezone, slot.getTherapist().getTimezone());
        var zonedDateTime = slot.getAvailableAt()
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of(effectiveTimezone));

        int dayOfWeek = zonedDateTime.getDayOfWeek().getValue();
        int day = zonedDateTime.getDayOfMonth();
        int month = zonedDateTime.getMonthValue();

        Locale locale = localeCode != null ? Locale.forLanguageTag(localeCode.replace("ua", "uk")) : Locale.ENGLISH;
        String dayName = dateLocalizedHelper.getWeekDayNameByNumber(dayOfWeek, locale.getLanguage())
            .toLowerCase(locale);
        String monthName = dateLocalizedHelper.getMonthNameByNumberInclined(month, locale.getLanguage())
            .toLowerCase(locale);

        String date = String.format("%s, %d %s", dayName, day, monthName);
        String time = zonedDateTime.format(TIME_FORMATTER);

        return new DateTimeInfo(date, time);
    }

    private String resolveTimezone(String userTimezone, String orderTimezone, String therapistTimezone) {
        if (userTimezone != null && timezoneHelper.isValidTimezone(userTimezone)) {
            return userTimezone;
        }
        if (orderTimezone != null && timezoneHelper.isValidTimezone(orderTimezone)) {
            return orderTimezone;
        }
        if (therapistTimezone != null && timezoneHelper.isValidTimezone(therapistTimezone)) {
            return therapistTimezone;
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }

    private record DateTimeInfo(String date, String time) {}
}

