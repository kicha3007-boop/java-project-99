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

class UsersControllerTest extends BaseControllerTest {

    @Test
    void testIndex() throws Exception {
        var body =
                mockMvc.perform(get("/api/users").with(token))
                        .andExpect(status().isOk())
                        .andExpect(header().exists("X-Total-Count"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body).isArray().isNotEmpty();
        assertThat(body).contains(testUser.getEmail()).doesNotContain("password");
    }

    @Test
    void testShow() throws Exception {
        var body =
                mockMvc.perform(get("/api/users/" + testUser.getId()).with(token))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body)
                .and(
                        v -> v.node("id").isEqualTo(testUser.getId()),
                        v -> v.node("email").isEqualTo(testUser.getEmail()),
                        v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                        v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                        v -> v.node("createdAt").isPresent(),
                        v -> v.node("password").isAbsent(),
                        v -> v.node("passwordDigest").isAbsent());
    }

    @Test
    void testShowMissing() throws Exception {
        mockMvc.perform(get("/api/users/999999").with(token)).andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var data =
                Map.of(
                        "email", "jack@google.com",
                        "firstName", "Jack",
                        "lastName", "Jons",
                        "password", "some-password");

        var body =
                mockMvc.perform(
                                post("/api/users")
                                        .with(token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(data)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThatJson(body).node("email").isEqualTo("jack@google.com");
        assertThatJson(body).node("password").isAbsent();
        var user = userRepository.findByEmail("jack@google.com").orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Jack");
        assertThat(user.getPasswordDigest()).isNotEqualTo("some-password");
    }

    @Test
    void testCreateInvalid() throws Exception {
        var data = Map.of("email", "not-an-email", "password", "12");

        mockMvc.perform(
                        post("/api/users")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findByEmail("not-an-email")).isEmpty();
    }

    @Test
    void testUpdatePartially() throws Exception {
        var data = Map.of("email", "new-" + testUser.getEmail(), "password", "new-password");

        mockMvc.perform(
                        put("/api/users/" + testUser.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getEmail()).isEqualTo("new-" + testUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getPasswordDigest()).isNotEqualTo(testUser.getPasswordDigest());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        mockMvc.perform(
                        put("/api/users/" + testUser.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("email", "broken"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOtherUserForbidden() throws Exception {
        var other = createUser();

        mockMvc.perform(
                        put("/api/users/" + other.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(Map.of("firstName", "Hacker"))))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(other.getId()).orElseThrow().getFirstName())
                .isEqualTo(other.getFirstName());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    void testDeleteOtherUserForbidden() throws Exception {
        var other = createUser();

        mockMvc.perform(delete("/api/users/" + other.getId()).with(token))
                .andExpect(status().isForbidden());

        assertThat(userRepository.existsById(other.getId())).isTrue();
    }

    @Test
    void testDeleteUserWithTasks() throws Exception {
        createTask(createStatus(), testUser);

        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isUnprocessableEntity());

        assertThat(userRepository.existsById(testUser.getId())).isTrue();
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
    }
}
