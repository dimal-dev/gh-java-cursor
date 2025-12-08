package com.goodhelp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Custom configuration properties for the GoodHelp application.
 * Binds to properties prefixed with "goodhelp" in application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "goodhelp")
public class GoodHelpProperties {
    
    private String defaultLocale = "en";
    private List<String> supportedLocales = List.of("en", "uk", "ru");
    private String defaultTimezone = "Europe/Kiev";
    
    private WayForPay wayforpay = new WayForPay();
    private Telegram telegram = new Telegram();
    
    @Data
    public static class WayForPay {
        private String merchantLogin;
        private String merchantSecretKey;
        private String merchantDomain;
    }
    
    @Data
    public static class Telegram {
        private String botToken;
        private boolean enabled = false;
    }
}
