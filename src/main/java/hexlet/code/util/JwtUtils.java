package hexlet.code.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtEncoder encoder;

    public String generateToken(String email) {
        var now = Instant.now();
        var claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(now.plus(1, ChronoUnit.DAYS))
                        .subject(email)
                        .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
