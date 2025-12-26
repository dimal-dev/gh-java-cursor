package com.goodhelp.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for the application.
 * Provides consistent error handling across all controllers.
 * Only active when custom error pages are enabled via configuration.
 * When disabled, Spring's default error handling (Whitelabel Error Page) will
 * be used.
 */
@ControllerAdvice
@ConditionalOnProperty(name = "goodhelp.error-handling.use-custom-pages", havingValue = "true", matchIfMissing = false)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException ex, Model model) {
        log.warn("No handler found: {}", ex.getRequestURL());
        return "error/404";
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(BusinessException ex, Model model) {
        log.warn("Business exception: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("message", "An unexpected error occurred");
        model.addAttribute("exception", ex);
        model.addAttribute("exceptionType", ex.getClass().getName());
        model.addAttribute("exceptionMessage", ex.getMessage());

        // Add stack trace for development
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        model.addAttribute("stackTrace", sw.toString());

        return "error/500";
    }
}
