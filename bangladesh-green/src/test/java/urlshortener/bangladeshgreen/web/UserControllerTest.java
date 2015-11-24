package urlshortener.bangladeshgreen.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
public class UserControllerTest{

    private User request;
    private UserController controller;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        // Creates one testing UserRequest (for registering users)
        request = new User();
        request.setEmail("test@testing.com");
        request.setPassword("testingPassword");
        request.setRealName("Testing");
        request.setRole("test");
        request.setUsername("test");
        // Initializes the controller
        controller = new UserController(userRepository);
    }

    @Test
    public void testRegisterResponses() throws Exception {
        // Register a test user
        ResponseEntity<? extends JsonResponse> response =  controller.register(request);
        // Check that new user has been registered
        assertEquals(201,response.getStatusCode().value());
        // Try to register an existing user
        request.setPassword("testingPassword");
        response =  controller.register(request);
        // Check that the register could not be possible
        assertEquals(409,response.getStatusCode().value());
    }

    @Test
    public void testRegisterRequests() throws Exception {
        // Check for empty contents
        request.setEmail("");
        ResponseEntity<? extends JsonResponse> response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request.setUsername("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request.setPassword("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());

        // Check for null contents
        request.setEmail(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request.setUsername(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request.setPassword(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
    }

    @After
    public void tearDown() throws Exception {
        // Deletes the test user
        User u = new User();
        u.setEmail("test@testing.com");
        u.setPassword("testingPassword");
        u.setRealName("Testing");
        u.setRole("test");
        u.setUsername("test");
        userRepository.delete(u);
    }
}