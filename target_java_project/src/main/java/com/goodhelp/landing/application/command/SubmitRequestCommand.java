package com.goodhelp.landing.application.command;

import java.util.List;

/**
 * Command to submit a therapist request.
 */
public record SubmitRequestCommand(
        String name,
        String email,
        String phone,
        List<String> topics,
        String message,
        String preferredTime,
        String sex,
        String channel,
        String consultationType,
        String price,
        String promocode,
        Long therapistId,
        Integer lgbtq,
        String locale
) {
    /**
     * Create command from form data.
     */
    public static SubmitRequestCommand from(
            String name,
            String email,
            String phone,
            List<String> topics,
            String message,
            String preferredTime,
            String sex,
            String channel,
            String consultationType,
            String price,
            String promocode,
            Long therapistId,
            Integer lgbtq,
            String locale) {
        return new SubmitRequestCommand(
                name,
                email,
                phone,
                topics != null ? topics : List.of(),
                message,
                preferredTime,
                sex != null ? sex : "both",
                channel,
                consultationType,
                price,
                promocode,
                therapistId,
                lgbtq != null ? lgbtq : 0,
                locale
        );
    }
}

