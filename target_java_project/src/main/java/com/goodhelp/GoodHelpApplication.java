package com.goodhelp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main entry point for the GoodHelp psychology consultation platform.
 * 
 * This application provides:
 * - Public landing pages for browsing therapists
 * - User cabinet for managing consultations
 * - Therapist cabinet for managing schedules and clients
 * - Staff/admin cabinet for platform management
 * - Billing integration for payment processing
 * - Notification system via Telegram and email
 */
@SpringBootApplication
@EnableScheduling
public class GoodHelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodHelpApplication.class, args);
    }
}
