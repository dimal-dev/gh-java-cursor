package com.goodhelp.user.infrastructure.security;

import com.goodhelp.user.domain.model.User;
import com.goodhelp.user.domain.model.UserAutologinToken;
import com.goodhelp.user.domain.repository.UserAutologinTokenRepository;
import com.goodhelp.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter for user auto-login authentication via token.
 * 
 * <p>Authentication flow:</p>
 * <ol>
 *   <li>User receives email with auto-login link</li>
 *   <li>Link contains token: /user/auto-login?t={token}</li>
 *   <li>Filter intercepts the request</li>
 *   <li>Token is validated against database</li>
 *   <li>If valid, SecurityContext is populated with GoodHelpUserDetails</li>
 *   <li>User is redirected to user dashboard</li>
 * </ol>
 * 
 * <p>This is a passwordless authentication mechanism commonly used
 * by users for quick access via their email.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAutoLoginFilter extends OncePerRequestFilter {

    private static final String TOKEN_PARAM = "t";
    private static final String AUTO_LOGIN_PATH = "/user/auto-login";
    private static final String SUCCESS_REDIRECT = "/user/";
    private static final String FAILURE_REDIRECT = "/user/login?error=invalid_token";

    private final UserAutologinTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
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

        Optional<User> userOpt = findUserByToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            authenticateUser(request, user);
            log.info("Auto-login successful for user: {} ({})", 
                user.getId(), user.getEmail());
            response.sendRedirect(SUCCESS_REDIRECT);
        } else {
            log.warn("Auto-login failed: invalid token");
            response.sendRedirect(FAILURE_REDIRECT);
        }
    }

    /**
     * Find user by auto-login token.
     */
    private Optional<User> findUserByToken(String token) {
        return tokenRepository.findByToken(token)
            .map(UserAutologinToken::getUser)
            .map(User::getId)
            .flatMap(userRepository::findById);
    }

    /**
     * Set up Spring Security authentication for the user.
     */
    private void authenticateUser(HttpServletRequest request, User user) {
        GoodHelpUserDetails userDetails = GoodHelpUserDetails.from(user);
        
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
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // Only process requests matching the auto-login path
        String path = request.getRequestURI();
        return !path.startsWith("/user/");
    }
}

