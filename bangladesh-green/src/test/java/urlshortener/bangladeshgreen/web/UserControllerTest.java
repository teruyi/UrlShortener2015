package urlshortener.bangladeshgreen.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.config.SpringMongoConfig;
import urlshortener.bangladeshgreen.config.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.domain.messages.JsonResponse;
import urlshortener.bangladeshgreen.repository.UserRepository;

import static org.junit.Assert.assertEquals;

/**
 * Class for testing the UserController.
 * It tests all possible operations, requests and responses.
 * Ensures that UserController is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class UserControllerTest{

    private UserController controller;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        // Initializes the controller
        controller = new UserController(userRepository);
    }

    /**
     * Creates a new test user.
     * @return User for testing.
     */
    public User createTestUser(){
        User request = new User();
        request.setEmail("test@testing.com");
        request.setPassword("testingPassword");
        request.setRealName("Testing");
        request.setRole("test");
        request.setUsername("test");
        return request;
    }

    @Test
    public void testUserRegister() throws Exception {
        // Register a test user
        ResponseEntity<? extends JsonResponse> response =  controller.register(createTestUser());
        // Check that new user has been registered
        assertEquals(201,response.getStatusCode().value());
    }

    @Test
    public void testExistingUsernameRegister() throws Exception {
        // Register a test user
        ResponseEntity<? extends JsonResponse> response =  controller.register(createTestUser());
        // Check that new user has been registered (with same username)
        assertEquals(201,response.getStatusCode().value());
        User request = createTestUser();
        request.setEmail("newEmail");
        response =  controller.register(request);
        // Check that the register could not be possible
        assertEquals(409,response.getStatusCode().value());
        request = createTestUser();
        request.setEmail("newEmail");
        userRepository.delete(request);
    }

    @Test
    public void testExistingEmailRegister() throws Exception {
        // Register a test user
        ResponseEntity<? extends JsonResponse> response =  controller.register(createTestUser());
        // Check that new user has been registered (with same email)
        assertEquals(201,response.getStatusCode().value());
        User request = createTestUser();
        request.setUsername("newUsername");
        response =  controller.register(request);
        // Check that the register could not be possible
        assertEquals(409,response.getStatusCode().value());
        request = createTestUser();
        request.setUsername("newUsername");
        userRepository.delete(request);
    }

    @Test
    public void testRegisterEmptyRequests() throws Exception {
        // Check for empty contents
        User request = createTestUser();
        request.setEmail("");
        ResponseEntity<? extends JsonResponse> response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request = createTestUser();
        request.setUsername("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request = createTestUser();
        request.setPassword("");
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
    }

    @Test
    public void testRegisterNullRequests() throws Exception {
        // Check for null contents
        User request = createTestUser();
        request.setEmail(null);
        ResponseEntity<? extends JsonResponse> response = controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request = createTestUser();
        request.setUsername(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
        // Another required field
        request = createTestUser();
        request.setPassword(null);
        response =  controller.register(request);
        assertEquals(400,response.getStatusCode().value());
    }

    @After
    public void tearDown() throws Exception {
        // Deletes the test user
        userRepository.delete(createTestUser());
    }
}