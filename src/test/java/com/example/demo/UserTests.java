package com.example.demo;

import com.example.demo.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class UserTests {
	@Autowired
	protected MockMvc mvc;
	private User user;
	ObjectMapper mapper = new ObjectMapper();

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
