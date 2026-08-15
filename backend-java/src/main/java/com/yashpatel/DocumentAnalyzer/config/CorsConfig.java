package com.yashpatel.DocumentAnalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8081",
                                "https://document-analyzer-frontend.jollytree-1bdfd601.southindia.azurecontainerapps.io")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}