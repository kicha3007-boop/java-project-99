package hexlet.code.service;

import hexlet.code.dto.taskstatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatuses.TaskStatusDTO;
import hexlet.code.dto.taskstatuses.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper mapper;

    public List<TaskStatusDTO> findAll() {
        return repository.findAll().stream().map(mapper::map).toList();
    }

    public TaskStatusDTO findById(Long id) {
        return mapper.map(getStatus(id));
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var status = mapper.map(data);
        repository.saveAndFlush(status);
        return mapper.map(status);
    }

    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO data) {
        var status = getStatus(id);
        mapper.update(data, status);
        repository.saveAndFlush(status);
        return mapper.map(status);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getStatus(id));
        repository.flush();
    }

    private TaskStatus getStatus(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Task status with id " + id + " not found"));
    }
}
