package hexlet.code.mapper;

import hexlet.code.dto.taskstatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatuses.TaskStatusDTO;
import hexlet.code.dto.taskstatuses.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {

    public TaskStatusDTO map(TaskStatus status) {
        var dto = new TaskStatusDTO();
        dto.setId(status.getId());
        dto.setName(status.getName());
        dto.setSlug(status.getSlug());
        dto.setCreatedAt(status.getCreatedAt());
        return dto;
    }

    public TaskStatus map(TaskStatusCreateDTO data) {
        var status = new TaskStatus();
        status.setName(data.getName());
        status.setSlug(data.getSlug());
        return status;
    }

    public void update(TaskStatusUpdateDTO data, TaskStatus status) {
        data.getName().ifPresent(status::setName);
        data.getSlug().ifPresent(status::setSlug);
    }
}
