package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskParamsDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final TaskSpecification specification;

    @Transactional(readOnly = true)
    public List<TaskDTO> findAll(TaskParamsDTO params) {
        return repository.findAll(specification.build(params)).stream().map(mapper::map).toList();
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(Long id) {
        return mapper.map(getTask(id));
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO data) {
        var task = mapper.map(data);
        repository.saveAndFlush(task);
        return mapper.map(task);
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO data) {
        var task = getTask(id);
        mapper.update(data, task);
        repository.saveAndFlush(task);
        return mapper.map(task);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getTask(id));
    }

    private Task getTask(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }
}
