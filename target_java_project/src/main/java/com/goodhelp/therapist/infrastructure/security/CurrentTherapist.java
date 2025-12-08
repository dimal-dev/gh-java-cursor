package com.goodhelp.therapist.infrastructure.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Annotation to inject the currently authenticated therapist into controller methods.
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * @GetMapping("/schedule")
 * public String schedule(@CurrentTherapist TherapistUserDetails therapist) {
 *     Long therapistId = therapist.getId();
 *     // ...
 * }
 * }
 * </pre>
 * 
 * <p>This is a convenience annotation wrapping Spring Security's
 * {@link AuthenticationPrincipal} annotation.</p>
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentTherapist {
}

