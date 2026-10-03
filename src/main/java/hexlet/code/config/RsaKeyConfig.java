package hexlet.code.config;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Пара ключей подписи JWT. Ключи читаются из окружения (RSA_PUBLIC_KEY, RSA_PRIVATE_KEY в PEM); без
 * них пара создаётся при старте — токены тогда живут до перезапуска приложения.
 */
@Configuration
public class RsaKeyConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RsaKeyConfig.class);

    @Bean
    public KeyPair jwtKeyPair(
            @Value("${rsa.public-key:}") String publicKeyPem,
            @Value("${rsa.private-key:}") String privateKeyPem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (publicKeyPem.isBlank() || privateKeyPem.isBlank()) {
            LOG.warn(
                    "RSA_PUBLIC_KEY / RSA_PRIVATE_KEY are not set, generating a temporary key pair");
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        }
        var keyFactory = KeyFactory.getInstance("RSA");
        var publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodePem(publicKeyPem)));
        var privateKey =
                keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodePem(privateKeyPem)));
        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public RSAPublicKey jwtPublicKey(KeyPair jwtKeyPair) {
        return (RSAPublicKey) jwtKeyPair.getPublic();
    }

    @Bean
    public RSAPrivateKey jwtPrivateKey(KeyPair jwtKeyPair) {
        return (RSAPrivateKey) jwtKeyPair.getPrivate();
    }

    private static byte[] decodePem(String pem) {
        var base64 = pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }
}
