package id.ac.ui.cs.gatherlove.auth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import id.ac.ui.cs.gatherlove.auth.config.AppJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;
    private AppJwtProperties appJwtProperties;

    @BeforeEach
    void setUp() {
        appJwtProperties = new AppJwtProperties();
        appJwtProperties.setKey("12345678901234567890123456789012");
        appJwtProperties.setAlgorithm("HS256");
        appJwtProperties.setIssuer("gatherlove");
        appJwtProperties.setExpiresIn(Duration.ofMinutes(15));

        JwtDecoder mockDecoder = Mockito.mock(JwtDecoder.class);
        jwtService = new JwtService(appJwtProperties, mockDecoder);
    }

    @Test
    void testGenerateJWTReturnsSignedToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "abc123");
        claims.put("role", "USER");

        String token = jwtService.generateJWT(claims);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT structure: header.payload.signature
    }

    @Test
    void testGenerateJWTThrowsRuntimeExceptionWhenKeyInvalid() {
        // Set invalid short key (e.g., < 32 bytes)
        appJwtProperties.setKey("short-key");

        JwtService brokenService = new JwtService(appJwtProperties, Mockito.mock(JwtDecoder.class));
        Map<String, Object> claims = Map.of("email", "broken@example.com");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> brokenService.generateJWT(claims));
        assertThat(ex.getMessage()).contains("Unable to generate JWT");
    }
}
