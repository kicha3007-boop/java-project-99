package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

class TasksControllerTest extends BaseControllerTest {

    private TaskStatus status;
    private Label label;
    private Task task;

    @BeforeEach
    void setUpTask() {
        status = createStatus();
        label = createLabel();
        task = createTask(status, testUser, label);
    }

    private String getTasks(String query) throws Exception {
        return mockMvc.perform(get("/api/tasks" + query).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"));

        assertThatJson(getTasks(""))
                .isArray()
                .hasSize(1)
                .first()
                .node("title")
                .isEqualTo(task.getName());
    }

    @Test
    void testShow() throws Exception {
        var body =
                mockMvc.perform(get("/api/tasks/" + task.getId()).with(token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body)
                .and(
                        v -> v.node("id").isEqualTo(task.getId()),
                        v -> v.node("index").isEqualTo(task.getIndex()),
                        v -> v.node("title").isEqualTo(task.getName()),
                        v -> v.node("content").isEqualTo(task.getDescription()),
                        v -> v.node("status").isEqualTo(status.getSlug()),
                        v -> v.node("assignee_id").isEqualTo(testUser.getId()),
                        v -> v.node("taskLabelIds").isArray().containsExactly(label.getId()),
                        v -> v.node("createdAt").isPresent());
    }

    @Test
    void testShowMissing() throws Exception {
        mockMvc.perform(get("/api/tasks/999999").with(token)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testCreate() throws Exception {
        var data = new HashMap<String, Object>();
        data.put("index", 12);
        data.put("assignee_id", testUser.getId());
        data.put("title", "Test title");
        data.put("content", "Test content");
        data.put("status", status.getSlug());
        data.put("taskLabelIds", List.of(label.getId()));

        var body =
                mockMvc.perform(
                                post("/api/tasks")
                                        .with(token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(data)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body).node("title").isEqualTo("Test title");
        var id = om.readTree(body).get("id").asLong();
        var created = taskRepository.findById(id).orElseThrow();
        assertThat(created.getDescription()).isEqualTo("Test content");
        assertThat(created.getTaskStatus().getSlug()).isEqualTo(status.getSlug());
        assertThat(created.getAssignee().getId()).isEqualTo(testUser.getId());
        assertThat(created.getLabels()).extracting(Label::getId).containsExactly(label.getId());
    }

    @Test
    void testCreateWithoutTitle() throws Exception {
        var data = Map.of("status", status.getSlug());

        mockMvc.perform(
                        post("/api/tasks")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWithUnknownStatus() throws Exception {
        var data = Map.of("title", "Task", "status", "no_such_status");

        mockMvc.perform(
                        post("/api/tasks")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void testUpdatePartially() throws Exception {
        var data = Map.of("title", "New title", "content", "New content");

        mockMvc.perform(
                        put("/api/tasks/" + task.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New title");
        assertThat(updated.getDescription()).isEqualTo("New content");
        assertThat(updated.getIndex()).isEqualTo(task.getIndex());
        assertThat(updated.getTaskStatus().getId()).isEqualTo(status.getId());
        assertThat(updated.getAssignee().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    void testUpdateRelations() throws Exception {
        var otherStatus = createStatus();
        var data = new HashMap<String, Object>();
        data.put("status", otherStatus.getSlug());
        data.put("assignee_id", null);
        data.put("taskLabelIds", List.of());

        mockMvc.perform(
                        put("/api/tasks/" + task.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getTaskStatus().getId()).isEqualTo(otherStatus.getId());
        assertThat(updated.getAssignee()).isNull();
        assertThat(updated.getLabels()).isEmpty();
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }

    @Test
    void testFilter() throws Exception {
        var otherUser = createUser();
        var otherStatus = createStatus();
        var otherTask = createTask(otherStatus, otherUser);
        otherTask.setName("Create new version");
        taskRepository.save(otherTask);

        assertThatJson(getTasks("?titleCont=new VERSION"))
                .isArray()
                .hasSize(1)
                .first()
                .node("id")
                .isEqualTo(otherTask.getId());
        assertThatJson(getTasks("?assigneeId=" + testUser.getId()))
                .isArray()
                .hasSize(1)
                .first()
                .node("id")
                .isEqualTo(task.getId());
        assertThatJson(getTasks("?status=" + otherStatus.getSlug()))
                .isArray()
                .hasSize(1)
                .first()
                .node("id")
                .isEqualTo(otherTask.getId());
        assertThatJson(getTasks("?labelId=" + label.getId()))
                .isArray()
                .hasSize(1)
                .first()
                .node("id")
                .isEqualTo(task.getId());
        assertThatJson(
                        getTasks(
                                "?titleCont=version&assigneeId="
                                        + testUser.getId()
                                        + "&status="
                                        + status.getSlug()))
                .isArray()
                .isEmpty();
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/tasks/" + task.getId())).andExpect(status().isUnauthorized());
    }
}
