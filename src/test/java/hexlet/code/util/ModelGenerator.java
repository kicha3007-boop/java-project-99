package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

/** Генераторы тестовых сущностей: уникальные имена, связи заполняет сам тест. */
public final class ModelGenerator {

    private static final Faker FAKER = new Faker();

    private ModelGenerator() {}

    public static Model<User> userModel() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getTasks))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(
                        Select.field(User::getEmail),
                        () -> FAKER.internet().uuid() + "@example.com")
                .supply(Select.field(User::getFirstName), () -> FAKER.name().firstName())
                .supply(Select.field(User::getLastName), () -> FAKER.name().lastName())
                .supply(Select.field(User::getPasswordDigest), () -> FAKER.internet().password())
                .toModel();
    }

    public static Model<TaskStatus> taskStatusModel() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getTasks))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(
                        Select.field(TaskStatus::getName),
                        () -> "Status " + FAKER.internet().uuid())
                .supply(Select.field(TaskStatus::getSlug), () -> "slug_" + FAKER.internet().uuid())
                .toModel();
    }

    public static Model<Label> labelModel() {
        return Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getTasks))
                .ignore(Select.field(Label::getCreatedAt))
                .supply(Select.field(Label::getName), () -> "label " + FAKER.internet().uuid())
                .toModel();
    }

    public static Model<Task> taskModel() {
        return Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getLabels))
                .ignore(Select.field(Task::getCreatedAt))
                .supply(Select.field(Task::getName), () -> FAKER.lorem().sentence(3))
                .supply(Select.field(Task::getDescription), () -> FAKER.lorem().paragraph())
                .supply(Select.field(Task::getIndex), () -> FAKER.number().numberBetween(1, 10_000))
                .toModel();
    }
}
