package com.example.aiinterviewassistant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${app.allowedOrigins:*}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有头部、方法和来源（开发环境）
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOriginPattern("*"); // 使用 pattern 而不是 origin
        
        // 允许凭据
        config.setAllowCredentials(true);
        
        // 暴露常见头部
        config.addExposedHeader("Content-Length");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("Date");
        config.addExposedHeader("Server");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
