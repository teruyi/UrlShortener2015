package urlshortener.bangladeshgreen.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.config.SpringMongoConfig;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.domain.messages.JsonResponse;
import urlshortener.bangladeshgreen.domain.messages.UserRequest;
import urlshortener.bangladeshgreen.repository.UserRepository;

import static org.junit.Assert.*;

/**
 * Created by piraces on 23/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SpringMongoConfig.class})
public class UserControllerTest {

    private UserRequest request;
    private UserController controller;
    private User user;

    @Before
    public void setUp() throws Exception {
        /*// Creates one testing UserRequest (for registering users)
        request = new UserRequest();
        request.setEmail("test@testing.com");
        request.setPassword("testingPassword");
        request.setRealName("Testing");
        request.setRole("test");
        request.setUsername("test");
        // Initializes the controller
        controller = new UserController();
        // Creates the user
        user = new User(request.getUsername(),request.getEmail(),request.getRole(),
                request.getPassword(),request.getRealName());
                */
    }

    @Test
    public void testRegisterResponses() throws Exception {
        /*// Register a test user
        ResponseEntity<? extends JsonResponse> response =  controller.register(request);
        // Check that new user has been registered
        assertEquals(201,response.getStatusCode());
        // Try to register an existing user
        response =  controller.register(request);
        // Check that the register could not be possible
        assertEquals(409,response.getStatusCode());
        */
    }

    @Test
    public void testRegisterRequests() throws Exception {
        /*// Check for empty contents
        request.setEmail("");
        ResponseEntity<? extends JsonResponse> response =  controller.register(request);
        assertEquals(400,response.getStatusCode());
        // Another required field
        request.setUsername("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode());
        // Another required field
        request.setPassword("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode());

        // Check for null contents
        request.setEmail(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode());
        // Another required field
        request.setUsername(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode());
        // Another required field
        request.setPassword(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode());
        */
    }

    @After
    public void tearDown() throws Exception {
        // Deletes the test user
        //userRepository.delete(user);
    }
}