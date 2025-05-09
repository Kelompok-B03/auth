package id.ac.ui.cs.gatherlove.auth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import id.ac.ui.cs.gatherlove.auth.config.AppJwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final AppJwtProperties appJwtProperties;
    private final JwtDecoder jwtDecoder;

    public String generateJWT(Map<String, Object> claims) {
        var key = appJwtProperties.getKey();
        var algorithm = appJwtProperties.getAlgorithm();

        var header = new JWSHeader(algorithm);
        var claimsSet = buildClaimsSet(claims);

        var jwt = new SignedJWT(header, claimsSet);

        try {
            var signer = new MACSigner(key);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (JOSEException e) {
            log.error("Error signing JWT: {}", e.getMessage());
            throw new RuntimeException("Unable to generate JWT", e);
        }
    }

    private JWTClaimsSet buildClaimsSet(Map<String, Object> claims) {
        var issuer = appJwtProperties.getIssuer();
        var issuedAt = Instant.now();
        var expirationTime = issuedAt.plus(appJwtProperties.getExpiresIn());

        var builder = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expirationTime));

        claims.forEach((key, value) -> {
            if ("roles".equals(key) && value instanceof List) {
                var roles = ((List<?>) value).stream()
                        .map(Object::toString)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .collect(Collectors.toList());
                builder.claim(key, roles);
            } else {
                builder.claim(key, value);
            }
        });

        return builder.build();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            var key = appJwtProperties.getKey();
            var verifier = new MACVerifier(key);

            if (!signedJWT.verify(verifier)) {
                log.warn("JWT signature verification failed");
                return false;
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                log.warn("JWT token expired");
                return false;
            }

            String issuer = claimsSet.getIssuer();
            if (appJwtProperties.getIssuer() != null &&
                    !appJwtProperties.getIssuer().equals(issuer)) {
                log.warn("JWT issuer validation failed");
                return false;
            }

            return true;
        } catch (ParseException e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            return false;
        } catch (JOSEException e) {
            log.error("Error verifying JWT signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse the JWT token and return a Jwt object.
     *
     * @param token the JWT token to be parsed
     * @return the parsed Jwt object
     */
    public Jwt parseJwt(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            throw new RuntimeException("Unable to parse JWT: " + e.getMessage(), e);
        }
    }
}