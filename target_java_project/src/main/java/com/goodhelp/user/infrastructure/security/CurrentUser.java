package com.goodhelp.user.infrastructure.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Annotation to inject the currently authenticated user into controller methods.
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * @GetMapping("/dashboard")
 * public String dashboard(@CurrentUser GoodHelpUserDetails user) {
 *     Long userId = user.getId();
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
public @interface CurrentUser {
}

