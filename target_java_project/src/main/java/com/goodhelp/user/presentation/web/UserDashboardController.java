package com.goodhelp.user.presentation.web;

import com.goodhelp.user.application.usecase.GetUserDashboardUseCase;
import com.goodhelp.user.application.dto.UserDashboardDto;
import com.goodhelp.user.infrastructure.security.GoodHelpUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for user dashboard.
 */
@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private static final String COOKIE_NAME_RULES_WAS_READ = "rules_was_read";

    private final GetUserDashboardUseCase dashboardUseCase;

    public UserDashboardController(GetUserDashboardUseCase dashboardUseCase) {
        this.dashboardUseCase = dashboardUseCase;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            @AuthenticationPrincipal GoodHelpUserDetails user,
            @RequestParam(name = "show-successfully-booked-consultation-message", required = false, defaultValue = "0") int showSuccessMessage,
            HttpServletRequest request,
            Model model) {
        
        // Check if rules were read (from cookie)
        boolean rulesRead = hasRulesCookie(request);
        
        // Get dashboard data
        UserDashboardDto dashboard = dashboardUseCase.execute(
            user.getId(),
            rulesRead,
            showSuccessMessage == 1
        );
        
        model.addAttribute("dashboard", dashboard);
        
        return "user/dashboard";
    }

    private boolean hasRulesCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME_RULES_WAS_READ.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }
}

