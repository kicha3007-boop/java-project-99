package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TaskStatusesControllerTest extends BaseControllerTest {

    @Test
    void testIndexContainsDefaults() throws Exception {
        var body =
                mockMvc.perform(get("/api/task_statuses").with(token))
                        .andExpect(status().isOk())
                        .andExpect(header().exists("X-Total-Count"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(body).contains("draft", "to_review", "to_be_fixed", "to_publish", "published");
    }

    @Test
    void testShow() throws Exception {
        var status = createStatus();

        var body =
                mockMvc.perform(get("/api/task_statuses/" + status.getId()).with(token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body)
                .and(
                        v -> v.node("id").isEqualTo(status.getId()),
                        v -> v.node("name").isEqualTo(status.getName()),
                        v -> v.node("slug").isEqualTo(status.getSlug()),
                        v -> v.node("createdAt").isPresent());
    }

    @Test
    void testShowMissing() throws Exception {
        mockMvc.perform(get("/api/task_statuses/999999").with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of("name", "New", "slug", "new");

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findBySlug("new")).isPresent();
    }

    @Test
    void testCreateInvalid() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDuplicateSlug() throws Exception {
        var data = Map.of("name", "Another draft", "slug", "draft");

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdatePartially() throws Exception {
        var status = createStatus();

        mockMvc.perform(
                        put("/api/task_statuses/" + status.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", "newStatus"))))
                .andExpect(status().isOk());

        var updated = taskStatusRepository.findById(status.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("newStatus");
        assertThat(updated.getSlug()).isEqualTo(status.getSlug());
    }

    @Test
    void testDelete() throws Exception {
        var status = createStatus();

        mockMvc.perform(delete("/api/task_statuses/" + status.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(status.getId())).isFalse();
    }

    @Test
    void testDeleteStatusWithTasks() throws Exception {
        var status = createStatus();
        createTask(status, null);

        mockMvc.perform(delete("/api/task_statuses/" + status.getId()).with(token))
                .andExpect(status().isUnprocessableEntity());

        assertThat(taskStatusRepository.existsById(status.getId())).isTrue();
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isUnauthorized());
        mockMvc.perform(
                        post("/api/task_statuses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", "x", "slug", "x"))))
                .andExpect(status().isUnauthorized());
    }
}
