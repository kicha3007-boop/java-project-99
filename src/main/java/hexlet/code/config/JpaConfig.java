package hexlet.code.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Включает заполнение createdAt / updatedAt через аннотации аудита. */
@Configuration
@EnableJpaAuditing
public class JpaConfig {}
