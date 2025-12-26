package com.goodhelp.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global advice to add common attributes to the model for view-rendering controllers.
 * 
 * <p>This advice is scoped to only {@code @Controller} classes (not {@code @RestController}),
 * ensuring that template-specific attributes are only added when rendering views.</p>
 * 
 * <p>This follows the existing pattern in the codebase where:
 * <ul>
 *   <li>Landing pages use {@code BaseLandingController} for context-specific attributes</li>
 *   <li>Global template attributes use scoped {@code @ControllerAdvice}</li>
 * </ul>
 * </p>
 * 
 * <p>Why scoped instead of global?
 * <ul>
 *   <li>REST endpoints don't need template model attributes</li>
 *   <li>More explicit about what controllers are affected</li>
 *   <li>Better separation of concerns</li>
 * </ul>
 * </p>
 */
@ControllerAdvice(annotations = Controller.class)
public class GlobalTemplateAdvice {

    /**
     * Adds the current request URI to the model for all view-rendering templates.
     * This allows templates to determine the active navigation item.
     * 
     * <p>This attribute is automatically available in all Thymeleaf templates
     * rendered by {@code @Controller} classes, eliminating the need to manually
     * add it in each controller method.</p>
     * 
     * @param request the HTTP servlet request (may be null in edge cases)
     * @return the request URI, or empty string if request is null
     */
    @ModelAttribute("currentUri")
    public String getCurrentUri(@Nullable HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return request.getRequestURI();
    }
}
