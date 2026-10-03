package hexlet.code.dto.labels;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {
    private JsonNullable<@NotNull @Size(min = 3, max = 1000) String> name =
            JsonNullable.undefined();
}
