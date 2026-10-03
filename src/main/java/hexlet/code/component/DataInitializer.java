package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Администратор, статусы и метки по умолчанию; повторный запуск ничего не задваивает. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    public static final String ADMIN_EMAIL = "hexlet@example.com";
    private static final String ADMIN_PASSWORD = "qwerty";

    /** Слаг → название, в порядке жизни задачи. */
    private static final List<Map.Entry<String, String>> DEFAULT_STATUSES =
            List.of(
                    Map.entry("draft", "Draft"),
                    Map.entry("to_review", "ToReview"),
                    Map.entry("to_be_fixed", "ToBeFixed"),
                    Map.entry("to_publish", "ToPublish"),
                    Map.entry("published", "Published"));

    private static final List<String> DEFAULT_LABELS = List.of("feature", "bug");

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            var admin = new User();
            admin.setEmail(ADMIN_EMAIL);
            admin.setPasswordDigest(passwordEncoder.encode(ADMIN_PASSWORD));
            userRepository.save(admin);
        }

        DEFAULT_STATUSES.forEach(
                entry -> {
                    if (taskStatusRepository.findBySlug(entry.getKey()).isEmpty()) {
                        var status = new TaskStatus();
                        status.setSlug(entry.getKey());
                        status.setName(entry.getValue());
                        taskStatusRepository.save(status);
                    }
                });

        DEFAULT_LABELS.forEach(
                name -> {
                    if (labelRepository.findByName(name).isEmpty()) {
                        var label = new Label();
                        label.setName(name);
                        labelRepository.save(label);
                    }
                });
    }
}
