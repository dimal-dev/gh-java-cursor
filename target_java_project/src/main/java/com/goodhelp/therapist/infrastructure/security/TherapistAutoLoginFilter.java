package com.goodhelp.therapist.infrastructure.security;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter for therapist auto-login authentication via token.
 * 
 * <p>Authentication flow:</p>
 * <ol>
 *   <li>Therapist receives email/Telegram with auto-login link</li>
 *   <li>Link contains token: /therapist/auto-login?t={token}</li>
 *   <li>Filter intercepts the request</li>
 *   <li>Token is validated against database</li>
 *   <li>If valid, SecurityContext is populated with TherapistUserDetails</li>
 *   <li>User is redirected to therapist dashboard</li>
 * </ol>
 * 
 * <p>This is a passwordless authentication mechanism commonly used
 * by therapists for quick access via their notification channels.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TherapistAutoLoginFilter extends OncePerRequestFilter {

    private static final String TOKEN_PARAM = "t";
    private static final String AUTO_LOGIN_PATH = "/therapist/auto-login";
    private static final String SUCCESS_REDIRECT = "/therapist/schedule";
    private static final String FAILURE_REDIRECT = "/therapist/login?error=invalid_token";

    private final TherapistAutologinTokenRepository tokenRepository;
    private final TherapistRepository therapistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (isAutoLoginRequest(request)) {
            handleAutoLogin(request, response);
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Check if this is an auto-login request.
     */
    private boolean isAutoLoginRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String token = request.getParameter(TOKEN_PARAM);
        return AUTO_LOGIN_PATH.equals(path) && token != null && !token.isBlank();
    }

    /**
     * Handle the auto-login authentication flow.
     */
    private void handleAutoLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String token = request.getParameter(TOKEN_PARAM);
        log.debug("Auto-login attempt with token: {}...", 
            token.length() > 8 ? token.substring(0, 8) + "..." : token);

        Optional<Therapist> therapistOpt = findTherapistByToken(token);

        if (therapistOpt.isPresent()) {
            Therapist therapist = therapistOpt.get();
            
            if (!therapist.isActive()) {
                log.warn("Auto-login failed: therapist {} is not active", therapist.getId());
                response.sendRedirect(FAILURE_REDIRECT);
                return;
            }

            authenticateTherapist(request, therapist);
            log.info("Auto-login successful for therapist: {} ({})", 
                therapist.getId(), therapist.getEmail());
            response.sendRedirect(SUCCESS_REDIRECT);
        } else {
            log.warn("Auto-login failed: invalid token");
            response.sendRedirect(FAILURE_REDIRECT);
        }
    }

    /**
     * Find therapist by auto-login token.
     */
    private Optional<Therapist> findTherapistByToken(String token) {
        return tokenRepository.findByToken(token)
            .filter(t -> t.matches(token)) // Constant-time comparison
            .map(TherapistAutologinToken::getTherapist)
            .map(Therapist::getId)
            .flatMap(therapistRepository::findById);
    }

    /**
     * Set up Spring Security authentication for the therapist.
     */
    private void authenticateTherapist(HttpServletRequest request, Therapist therapist) {
        TherapistUserDetails userDetails = TherapistUserDetails.from(therapist);
        
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // No credentials for token-based auth
                userDetails.getAuthorities()
            );
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Ensure session is created and authentication is stored
        request.getSession(true)
            .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only process requests matching the auto-login path
        String path = request.getRequestURI();
        return !path.startsWith("/therapist/");
    }
}

