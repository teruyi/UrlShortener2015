package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.UserRepository;
import static urlshortener.bangladeshgreen.web.fixture.UserFixture.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


/**
 * Class for testing the UserController.
 * It tests all possible operations, requests and responses.
 * Ensures that UserController is working correctly.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest{

    private MockMvc mockMvc;

    @InjectMocks
    private UserController controller;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    /*
     * Test that new user register is doing correctly and response is ok.
     */
    public void testUserRegister() throws Exception {
        // Register a test user
        User test = someUser();

        // Maps the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(test);

        // Do the post request to create a new user, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data.username",is(test.getUsername())))
                .andExpect(jsonPath("$.data.email",is(test.getEmail())))
                .andExpect(jsonPath("$.data.password",is(nullValue())))
                .andExpect(jsonPath("$.data.realName",is(test.getRealName())));
    }

    @Test
    /*
     * Test that existing username user register is not possible and response is error.
     */
    public void testExistingUsernameRegister() throws Exception {
        // Mock userRepository response to an existing user
        when(userRepository.findByUsername(someUser().getUsername())).thenReturn(someUser());
        User test = someUser();
        // Change the email of the user
        test.setEmail("newEmail@mail.com");

        // Maps the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with existing username, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status",is("error")));
    }

    @Test
    /*
     * Test that existing email user register is not possible and response is error.
     */
    public void testExistingEmailRegister() throws Exception {
        // Mock userRepository response to an existing user
        when(userRepository.findByEmail(someUser().getEmail())).thenReturn(someUser());
        User test = someUser();
        // Change the username of the user
        test.setUsername("newUsername");

        // Maps the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with existing email, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status",is("error")));
    }

    @Test
    /*
     * Test that empty required fields in user register is not possible and response is error.
     */
    public void testRegisterEmptyRequests() throws Exception {
        // Check for empty contents
        User test = someUser();
        // Set email empty for current user
        test.setEmail("");

        // Maps the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with empty email, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));

        // Another required field
        test = someUser();
        // Set username empty for current user
        test.setUsername("");

        // Maps the object to JSON
        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with empty username, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));

        // Another required field
        test = someUser();
        // Set password empty for current user
        test.setPassword("");

        // Maps the object to JSON
        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with empty password, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));
    }

    @Test
    /*
     * Test that null required fields in user register is not possible and response is error.
     */
    public void testRegisterNullRequests() throws Exception {
        // Check for null contents
        User test = someUser();
        // Set email null for current user
        test.setEmail(null);

        // Maps the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with null email, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));

        // Another required field
        test = someUser();
        // Set username null for current user
        test.setUsername(null);

        // Maps the object to JSON
        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with null username, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));

        // Another required field
        test = someUser();
        // Set password null for current user
        test.setPassword(null);

        // Maps the object to JSON
        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(test);

        // Do the post request to create a new user with null password, and checks the results
        mockMvc.perform(post("/user").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is("error")));
    }
}