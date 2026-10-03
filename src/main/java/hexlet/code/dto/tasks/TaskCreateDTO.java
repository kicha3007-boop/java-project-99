package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank private String title;

    private String content;

    @NotBlank private String status;

    private Set<Long> taskLabelIds = new HashSet<>();
}
