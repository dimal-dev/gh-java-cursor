package com.goodhelp.user.infrastructure.security;

import com.goodhelp.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security UserDetailsService for user authentication.
 * 
 * Loads user details by email for authentication purposes.
 * This service is used by Spring Security's authentication manager.
 */
@Service("userUserDetailsService")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GoodHelpUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        return userRepository.findByEmail(email.toLowerCase().trim())
            .map(GoodHelpUserDetails::from)
            .orElseThrow(() -> {
                log.warn("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found: " + email);
            });
    }

    /**
     * Load user by ID.
     * Used for session-based authentication after auto-login.
     */
    public GoodHelpUserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);
        
        return userRepository.findById(id)
            .map(GoodHelpUserDetails::from)
            .orElseThrow(() -> {
                log.warn("User not found with ID: {}", id);
                return new UsernameNotFoundException("User not found with ID: " + id);
            });
    }
}

