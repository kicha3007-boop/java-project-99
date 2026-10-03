package hexlet.code.dto.tasks;

import lombok.Getter;
import lombok.Setter;

/** Параметры фильтра списка задач из строки запроса. */
@Getter
@Setter
public class TaskParamsDTO {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;
}
