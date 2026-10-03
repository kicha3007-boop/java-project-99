package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
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

/** Администратор, статусы, метки и стартовая задача; повторный запуск ничего не задваивает. */
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
    private final TaskRepository taskRepository;
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

        // Доска фронтенда рисует колонку только для статуса, в котором есть задачи, а на пустом
        // списке — заглушку. Поэтому стартовая задача заводится в каждом статусе по умолчанию.
        if (taskRepository.count() == 0) {
            var index = 1;
            for (var entry : DEFAULT_STATUSES) {
                var task = new Task();
                task.setName("Example task: " + entry.getValue());
                task.setDescription("Drag the card between columns to change its status");
                task.setIndex(index++);
                task.setTaskStatus(taskStatusRepository.findBySlug(entry.getKey()).orElseThrow());
                taskRepository.save(task);
            }
        }
    }
}
