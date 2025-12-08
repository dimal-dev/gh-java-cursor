package com.goodhelp.common.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service for generating random tokens and passwords.
 * Migrated from PHP: App\Common\Service\PasswordGenerator
 */
@Service
public class PasswordGenerator {
    
    private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a random alphanumeric token.
     * 
     * @param length Length of the token
     * @return Random token string
     */
    public String generateToken(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
    
    /**
     * Generate a 32-character token (used for autologin tokens).
     * 
     * @return 32-character random token
     */
    public String generateAutologinToken() {
        return generateToken(32);
    }
}
