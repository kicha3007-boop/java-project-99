package hexlet.code.mapper;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public LabelDTO map(Label label) {
        var dto = new LabelDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        return dto;
    }

    public Label map(LabelCreateDTO data) {
        var label = new Label();
        label.setName(data.getName());
        return label;
    }

    public void update(LabelUpdateDTO data, Label label) {
        data.getName().ifPresent(label::setName);
    }
}
