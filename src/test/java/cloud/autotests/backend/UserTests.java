package cloud.autotests.backend;

import cloud.autotests.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserTests extends BaseTest {

    private User user;

    @BeforeEach
    void setup() {
        user = new User("test", "testov");
    }

    @Test
    void testGetUsersSucceeded() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUserSucceeded() throws Exception {
        String jsonInString = mapper.writeValueAsString(user);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInString))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetUserByIdSucceeded() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/users/iterkin"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/users/null"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserSucceeded() throws Exception {
        String jsonInString = mapper.writeValueAsString(user);

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInString))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserSucceeded() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/users/iterkin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
