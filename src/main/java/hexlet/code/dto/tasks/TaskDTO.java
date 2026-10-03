package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/** Имена полей — те, что ждёт фронтенд: title, content, status, assignee_id, taskLabelIds. */
@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
