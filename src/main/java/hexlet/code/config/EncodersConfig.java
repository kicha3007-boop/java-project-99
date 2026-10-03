package hexlet.code.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class EncodersConfig {

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey jwtPublicKey) {
        return NimbusJwtDecoder.withPublicKey(jwtPublicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey jwtPublicKey, RSAPrivateKey jwtPrivateKey) {
        var jwk = new RSAKey.Builder(jwtPublicKey).privateKey(jwtPrivateKey).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
