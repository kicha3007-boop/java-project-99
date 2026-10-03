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

class LabelsControllerTest extends BaseControllerTest {

    @Test
    void testIndexContainsDefaults() throws Exception {
        var body =
                mockMvc.perform(get("/api/labels").with(token))
                        .andExpect(status().isOk())
                        .andExpect(header().exists("X-Total-Count"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(body).contains("\"feature\"", "\"bug\"");
    }

    @Test
    void testShow() throws Exception {
        var label = createLabel();

        var body =
                mockMvc.perform(get("/api/labels/" + label.getId()).with(token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body)
                .and(
                        v -> v.node("id").isEqualTo(label.getId()),
                        v -> v.node("name").isEqualTo(label.getName()),
                        v -> v.node("createdAt").isPresent());
    }

    @Test
    void testShowMissing() throws Exception {
        mockMvc.perform(get("/api/labels/999999").with(token)).andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", "new label"))))
                .andExpect(status().isCreated());

        assertThat(labelRepository.findByName("new label")).isPresent();
    }

    @Test
    void testCreateTooShort() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", "ab"))))
                .andExpect(status().isBadRequest());

        assertThat(labelRepository.findByName("ab")).isEmpty();
    }

    @Test
    void testUpdate() throws Exception {
        var label = createLabel();

        mockMvc.perform(
                        put("/api/labels/" + label.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("name", "Renamed"))))
                .andExpect(status().isOk());

        assertThat(labelRepository.findById(label.getId()).orElseThrow().getName())
                .isEqualTo("Renamed");
    }

    @Test
    void testDelete() throws Exception {
        var label = createLabel();

        mockMvc.perform(delete("/api/labels/" + label.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(label.getId())).isFalse();
    }

    @Test
    void testDeleteLabelWithTasks() throws Exception {
        var label = createLabel();
        createTask(createStatus(), null, label);

        mockMvc.perform(delete("/api/labels/" + label.getId()).with(token))
                .andExpect(status().isUnprocessableEntity());

        assertThat(labelRepository.existsById(label.getId())).isTrue();
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
    }
}
