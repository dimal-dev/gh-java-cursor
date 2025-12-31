package com.goodhelp.config;

import com.goodhelp.therapist.infrastructure.security.TherapistAutoLoginFilter;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetailsService;
import com.goodhelp.user.infrastructure.security.GoodHelpUserDetailsService;
import com.goodhelp.user.infrastructure.security.UserAutoLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for GoodHelp application.
 * 
 * Defines multiple security filter chains for different areas:
 * - /therapist/** - Therapist authentication
 * - /staff/** - Staff/Admin authentication
 * - /user/** - User authentication
 * - /billing/webhook - Webhook signature verification
 * - /** - Public access
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TherapistAutoLoginFilter therapistAutoLoginFilter;
    private final TherapistUserDetailsService therapistUserDetailsService;
    private final UserAutoLoginFilter userAutoLoginFilter;
    private final GoodHelpUserDetailsService userUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider therapistAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(therapistUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_STAFF_SUPERUSER > ROLE_STAFF_USER");
        return hierarchy;
    }

    /**
     * Security filter chain for therapist cabinet (/therapist/**)
     * 
     * Supports two authentication methods:
     * 1. Auto-login via token link (primary method for therapists)
     * 2. Form-based login (fallback)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain therapistSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/therapist/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/therapist/login", "/therapist/auto-login").permitAll()
                        .requestMatchers("/therapist/request-login").permitAll()
                        .anyRequest().hasAnyRole("PSIHOLOG", "TEST_PSIHOLOG"))
                .authenticationProvider(therapistAuthenticationProvider())
                .addFilterBefore(therapistAutoLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/therapist/login")
                        .loginProcessingUrl("/therapist/login")
                        .defaultSuccessUrl("/therapist/schedule", true)
                        .failureUrl("/therapist/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/therapist/logout"))
                        .logoutSuccessUrl("/therapist/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/therapist/api/**"))
                .sessionManagement(session -> session
                        .maximumSessions(5)
                        .expiredUrl("/therapist/login?expired=true"));

        return http.build();
    }

    /**
     * Security filter chain for staff cabinet (/staff/**)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain staffSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/staff/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/staff/login", "/staff/auto-login").permitAll()
                        .anyRequest().hasRole("STAFF_USER"))
                .formLogin(form -> form
                        .loginPage("/staff/login")
                        .loginProcessingUrl("/staff/login")
                        .defaultSuccessUrl("/staff/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/staff/logout"))
                        .logoutSuccessUrl("/staff/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    /**
     * Security filter chain for user cabinet (/user/**)
     * 
     * Supports two authentication methods:
     * 1. Auto-login via token link (primary method for users)
     * 2. Form-based login (fallback)
     */
    @Bean
    @Order(3)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/user/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/auto-login").permitAll()
                        .anyRequest().hasRole("USER"))
                .authenticationProvider(userAuthenticationProvider())
                .addFilterBefore(userAutoLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/user/", true)
                        .failureUrl("/user/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/user/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .rememberMe(remember -> remember
                        .key("goodhelp-user-remember-me")
                        .tokenValiditySeconds(31104000) // ~1 year
                        .userDetailsService(userUserDetailsService));

        return http.build();
    }

    /**
     * Security filter chain for billing webhooks
     * Disables CSRF for webhook endpoints
     */
    @Bean
    @Order(4)
    public SecurityFilterChain billingSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/billing/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Security filter chain for notification webhooks
     */
    @Bean
    @Order(5)
    public SecurityFilterChain notificationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/notification/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Default security filter chain for public pages
     */
    @Bean
    @Order(100)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/en/**",
                                "/ru/**",
                                "/uk/**",
                                "/therapist-list",
                                "/json/therapist-list",
                                "/psiholog/**",
                                "/book-consultation/**",
                                "/api/book-consultation/**",
                                "/api/save-timezone",
                                "/checkout/**",
                                "/blog/**",
                                "/about",
                                "/prices",
                                "/privacy",
                                "/terms-of-use",
                                "/refund-policy",
                                "/consultation-conditions",
                                "/request-therapist/**",
                                "/psiholog-apply",
                                "/family-psiholog",
                                "/teenage-psiholog",
                                "/api/timezone",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/favicon.ico",
                                "/error")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"));

        return http.build();
    }
}
