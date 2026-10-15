package com.example.deber1camachosantiago.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// El front se abre en otro puerto (63342 de IntelliJ) y el navegador bloquea
// las peticiones al 8080 si el backend no lo permite. Esto es solo para desarrollo,
// en produccion habria que poner el dominio real del front.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "DELETE");
    }
}
