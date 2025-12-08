package com.goodhelp.therapist.infrastructure.security;

import com.goodhelp.therapist.domain.repository.TherapistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security UserDetailsService for therapist authentication.
 * 
 * Loads therapist details by email for authentication purposes.
 * This service is used by Spring Security's authentication manager.
 */
@Service("therapistUserDetailsService")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TherapistUserDetailsService implements UserDetailsService {

    private final TherapistRepository therapistRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading therapist by email: {}", email);
        
        return therapistRepository.findByEmail(email.toLowerCase().trim())
            .map(TherapistUserDetails::from)
            .orElseThrow(() -> {
                log.warn("Therapist not found with email: {}", email);
                return new UsernameNotFoundException("Therapist not found: " + email);
            });
    }

    /**
     * Load therapist by ID.
     * Used for session-based authentication after auto-login.
     */
    public TherapistUserDetails loadUserById(Long id) {
        log.debug("Loading therapist by ID: {}", id);
        
        return therapistRepository.findById(id)
            .map(TherapistUserDetails::from)
            .orElseThrow(() -> {
                log.warn("Therapist not found with ID: {}", id);
                return new UsernameNotFoundException("Therapist not found with ID: " + id);
            });
    }
}

