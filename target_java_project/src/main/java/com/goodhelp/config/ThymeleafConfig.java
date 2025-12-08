package com.goodhelp.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Thymeleaf configuration.
 * Adds layout dialect for template inheritance.
 */
@Configuration
public class ThymeleafConfig {
    
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
