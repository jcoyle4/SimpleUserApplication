package com.jamiecoyle.user.controllersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamiecoyle.user.controllers.UserController;

import com.jamiecoyle.user.models.User;
import com.jamiecoyle.user.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    /**
     * This test is failing and I am not sure why. Investigation would need to take place before
     * proceeding to write tests for other endpoints.
     */
    @Test
    void createUserValidInput() throws Exception{
        User user = new User("name", "email", "password");

        mockMvc.perform(post("/api/v1/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void createUserInvalidInput() throws Exception {
        User user = new User("name" ,null, null);

        mockMvc.perform(post("/api/v1/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest()); // currently throwing false positive

    }

}
