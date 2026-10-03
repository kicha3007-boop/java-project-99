package hexlet.code.specification;

import hexlet.code.dto.tasks.TaskParamsDTO;
import hexlet.code.model.Task;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/** Фильтр списка задач: каждое условие применяется, только если параметр передан. */
@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String substring) {
        return (root, query, cb) -> {
            if (substring == null || substring.isBlank()) {
                return cb.conjunction();
            }
            var pattern = "%" + substring.toLowerCase(Locale.ROOT) + "%";
            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String slug) {
        return (root, query, cb) ->
                slug == null || slug.isBlank()
                        ? cb.conjunction()
                        : cb.equal(root.get("taskStatus").get("slug"), slug);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            return cb.equal(root.join("labels").get("id"), labelId);
        };
    }
}
