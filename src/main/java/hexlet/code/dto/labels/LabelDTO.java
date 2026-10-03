package hexlet.code.dto.labels;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
