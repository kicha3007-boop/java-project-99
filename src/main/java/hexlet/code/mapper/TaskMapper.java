package hexlet.code.mapper;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.InvalidReferenceException;
import hexlet.code.model.BaseEntity;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Поля задачи и JSON называются по-разному: name ↔ title, description ↔ content, статус — слагом.
 */
@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public TaskDTO map(Task task) {
        var dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setIndex(task.getIndex());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setAssigneeId(task.getAssignee() == null ? null : task.getAssignee().getId());
        dto.setTitle(task.getName());
        dto.setContent(task.getDescription());
        dto.setStatus(task.getTaskStatus().getSlug());
        dto.setTaskLabelIds(
                task.getLabels().stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        return dto;
    }

    public Task map(TaskCreateDTO data) {
        var task = new Task();
        task.setIndex(data.getIndex());
        task.setName(data.getTitle());
        task.setDescription(data.getContent());
        task.setTaskStatus(findStatus(data.getStatus()));
        task.setAssignee(findAssignee(data.getAssigneeId()));
        task.setLabels(findLabels(data.getTaskLabelIds()));
        return task;
    }

    public void update(TaskUpdateDTO data, Task task) {
        data.getIndex().ifPresent(task::setIndex);
        data.getTitle().ifPresent(task::setName);
        data.getContent().ifPresent(task::setDescription);
        data.getStatus().ifPresent(slug -> task.setTaskStatus(findStatus(slug)));
        data.getAssigneeId().ifPresent(id -> task.setAssignee(findAssignee(id)));
        data.getTaskLabelIds().ifPresent(ids -> task.setLabels(findLabels(ids)));
    }

    private TaskStatus findStatus(String slug) {
        return taskStatusRepository
                .findBySlug(slug)
                .orElseThrow(() -> new InvalidReferenceException("Unknown task status: " + slug));
    }

    private User findAssignee(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository
                .findById(id)
                .orElseThrow(() -> new InvalidReferenceException("Unknown assignee: " + id));
    }

    private Set<Label> findLabels(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        var labels = new HashSet<>(labelRepository.findAllById(ids));
        if (labels.size() != ids.size()) {
            throw new InvalidReferenceException("Unknown labels: " + ids);
        }
        return labels;
    }
}
