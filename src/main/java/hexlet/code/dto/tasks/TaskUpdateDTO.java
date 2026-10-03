package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index = JsonNullable.undefined();

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();

    private JsonNullable<@NotBlank String> title = JsonNullable.undefined();

    private JsonNullable<String> content = JsonNullable.undefined();

    private JsonNullable<@NotBlank String> status = JsonNullable.undefined();

    private JsonNullable<Set<Long>> taskLabelIds = JsonNullable.undefined();
}
