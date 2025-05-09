package id.ac.ui.cs.gatherlove.auth.config;

import id.ac.ui.cs.gatherlove.auth.service.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    private final AppJwtProperties appJwtProperties;

    public JwtConfig(AppJwtProperties appJwtProperties) {
        this.appJwtProperties = appJwtProperties;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(appJwtProperties.getKey()).build();
    }
}
