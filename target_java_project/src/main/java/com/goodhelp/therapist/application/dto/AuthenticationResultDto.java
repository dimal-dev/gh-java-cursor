package com.goodhelp.therapist.application.dto;

/**
 * DTO for authentication result.
 */
public record AuthenticationResultDto(
    Long therapistId,
    String email,
    String fullName,
    String timezone,
    boolean success,
    String errorMessage
) {
    /**
     * Create successful authentication result.
     */
    public static AuthenticationResultDto success(Long therapistId, String email, 
                                                   String fullName, String timezone) {
        return new AuthenticationResultDto(therapistId, email, fullName, timezone, true, null);
    }

    /**
     * Create failed authentication result.
     */
    public static AuthenticationResultDto failure(String errorMessage) {
        return new AuthenticationResultDto(null, null, null, null, false, errorMessage);
    }

    /**
     * Check if authentication was successful.
     */
    public boolean isSuccess() {
        return success;
    }
}
