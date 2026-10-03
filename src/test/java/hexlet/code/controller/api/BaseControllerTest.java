package hexlet.code.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

/** Общая подготовка: чистая база (кроме начальных данных) и токен свежего пользователя. */
@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper om;
    @Autowired protected UserRepository userRepository;
    @Autowired protected TaskStatusRepository taskStatusRepository;
    @Autowired protected LabelRepository labelRepository;
    @Autowired protected TaskRepository taskRepository;

    protected User testUser;
    protected JwtRequestPostProcessor token;

    @BeforeEach
    void resetDatabase() {
        taskRepository.deleteAll();
        testUser = createUser();
        token = tokenFor(testUser);
    }

    protected JwtRequestPostProcessor tokenFor(User user) {
        return jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    protected User createUser() {
        return userRepository.save(Instancio.create(ModelGenerator.userModel()));
    }

    protected TaskStatus createStatus() {
        return taskStatusRepository.save(Instancio.create(ModelGenerator.taskStatusModel()));
    }

    protected Label createLabel() {
        return labelRepository.save(Instancio.create(ModelGenerator.labelModel()));
    }

    protected Task createTask(TaskStatus status, User assignee, Label... labels) {
        var task = Instancio.create(ModelGenerator.taskModel());
        task.setTaskStatus(status);
        task.setAssignee(assignee);
        task.getLabels().addAll(java.util.List.of(labels));
        return taskRepository.save(task);
    }
}
