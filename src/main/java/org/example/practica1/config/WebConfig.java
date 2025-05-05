package org.example.practica1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/products").setViewName("products/list");
        registry.addViewController("/orders").setViewName("orders/list");
        registry.addViewController("/categories").setViewName("categories/list");
        registry.addViewController("/login").setViewName("login");
    }
}
