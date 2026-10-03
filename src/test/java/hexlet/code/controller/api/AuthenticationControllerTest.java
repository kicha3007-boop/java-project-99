package hexlet.code.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.component.DataInitializer;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class AuthenticationControllerTest extends BaseControllerTest {

    private String login(Map<String, String> credentials) throws Exception {
        return om.writeValueAsString(credentials);
    }

    @Test
    void testLoginAndUseToken() throws Exception {
        var jwt =
                mockMvc.perform(
                                post("/api/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                login(
                                                        Map.of(
                                                                "username",
                                                                DataInitializer.ADMIN_EMAIL,
                                                                "password",
                                                                "qwerty"))))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(jwt.split("\\.")).hasSize(3);
        mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        login(
                                                Map.of(
                                                        "username",
                                                        DataInitializer.ADMIN_EMAIL,
                                                        "password",
                                                        "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithUnknownUser() throws Exception {
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        login(
                                                Map.of(
                                                        "username",
                                                        "nobody@example.com",
                                                        "password",
                                                        "qwerty"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testWelcomeIsPublic() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to Spring"));
    }
}
