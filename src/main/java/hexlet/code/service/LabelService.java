package hexlet.code.service;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    public List<LabelDTO> findAll() {
        return repository.findAll().stream().map(mapper::map).toList();
    }

    public LabelDTO findById(Long id) {
        return mapper.map(getLabel(id));
    }

    @Transactional
    public LabelDTO create(LabelCreateDTO data) {
        var label = mapper.map(data);
        repository.saveAndFlush(label);
        return mapper.map(label);
    }

    @Transactional
    public LabelDTO update(Long id, LabelUpdateDTO data) {
        var label = getLabel(id);
        mapper.update(data, label);
        repository.saveAndFlush(label);
        return mapper.map(label);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getLabel(id));
        repository.flush();
    }

    private Label getLabel(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Label with id " + id + " not found"));
    }
}
