package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.someUrlList;
import static urlshortener.bangladeshgreen.web.fixture.UserFixture.*;
import static org.mockito.Mockito.when;
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

    @Mock
    private ShortURLRepository shortURLRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        controller.sendEmails(false);
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


    @Test
    /**
     * Test that a user can view its own profile.
     */
    public void testAuthorizedUserVisualization() throws Exception{

        User someUser = someUser();
        when(userRepository.findByUsername("user")).thenReturn(someUser);

        //Test redirection
        mockMvc.perform(get("/user/{username}", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data.username",is(someUser.getUsername())))
                .andExpect(jsonPath("$.data.email",is(someUser.getEmail())))
                .andExpect(jsonPath("$.data.realName",is(someUser.getRealName())))
                .andExpect(jsonPath("$.data.role",is(someUser.getRole())));


    }

    @Test
    /**
     * Test that a user can not view another profile.
     */
    public void testUnauthorizedUserVisualization() throws Exception{

        User someUser = someUser();
        when(userRepository.findByUsername("user")).thenReturn(someUser);

        //Test redirection
        mockMvc.perform(get("/user/{username}", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("anotherUser","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Permission denied")));


    }

    @Test
    /**
     * Test that a user can delete its own profile
     */
    public void testAuthorizedDeletion() throws Exception{

        User someUser = someUser();
        when(userRepository.findByUsername("user")).thenReturn(someUser);

        //Test redirection
        mockMvc.perform(delete("/user/{username}", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data",is("User user has been deleted.")));


    }

    @Test
    /**
     * Test that a user can't delete another profile
     */
    public void testUnauthorizedDeletion() throws Exception{

        User someUser = someUser();
        when(userRepository.findByUsername("user")).thenReturn(someUser);

        //Test redirection
        mockMvc.perform(delete("/user/{username}", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("anotherUser","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message",is("Permission denied")));


    }



    @Test
    /*
    It tests a regular user updating its info.
    A regular user can update only the realname and password.
    Other fields are ignored.
     */
    public void testRegularAuthorizedUpdate() throws Exception{

        User someUserOld = someUser();
        User someUserNew = someUser();

        when(userRepository.findByUsername("user")).thenReturn(someUserOld);

        someUserNew.setRealName("updated-realname");
        someUserNew.setPassword("newPassword");
        someUserNew.setRole("updated-role");
        someUserNew.setEmail("updated-mail");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(someUserNew);
        //Test redirection
        mockMvc.perform(put("/user/{username}", "user").contentType("application/json")
                .content(json)
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data.username",is(someUserOld.getUsername())))
                .andExpect(jsonPath("$.data.email",is(someUserOld.getEmail())))
                .andExpect(jsonPath("$.data.password",is(nullValue())))
                .andExpect(jsonPath("$.data.realName",is(someUserNew.getRealName())))
                .andExpect(jsonPath("$.data.role",is(someUserOld.getRole())));


    }


    @Test
    /*
    It tests a admin updating info of a regular user..
    An admin can change every field except username.
    Username changes are ignored.
     */
    public void testAdminAuthorizedUpdate() throws Exception{

        User someUserOld = someUser();
        User someUserNew = someUser();

        when(userRepository.findByUsername("user")).thenReturn(someUserOld);

        someUserNew.setRealName("updated-realname");
        someUserNew.setPassword("newPassword");
        someUserNew.setRole("updated-role");
        someUserNew.setEmail("updated-mail");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(someUserNew);
        //Test redirection
        mockMvc.perform(put("/user/{username}", "user").contentType("application/json")
                .content(json)
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("admin","admin"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data.username",is(someUserOld.getUsername()))) //Remember, username can't change
                .andExpect(jsonPath("$.data.email",is(someUserNew.getEmail())))
                .andExpect(jsonPath("$.data.password",is(nullValue())))
                .andExpect(jsonPath("$.data.realName",is(someUserNew.getRealName())))
                .andExpect(jsonPath("$.data.role",is(someUserNew.getRole())));


    }



    @Test
    /**
     * Tests that admin can obtain a listing of all users.
     */
    public void testAuthorizedAllUsersVisualization() throws Exception{

        User someUser = someUser();
        User someUser2 = someUser2();
        List<User> userList = new ArrayList<User>();
        userList.add(someUser);
        userList.add(someUser2);

        when(userRepository.list()).thenReturn(userList);

        //Test
        mockMvc.perform(get("/user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("admin","admin"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data",hasSize(2)));


    }

    @Test
    /**
     * Tests that a non-admin user can't view a listing of all users.
     */
    public void testUnauthorizedAllUsersVisualization() throws Exception{

        User someUser = someUser();
        User someUser2 = someUser2();
        List<User> userList = new ArrayList<User>();
        userList.add(someUser);
        userList.add(someUser2);

        when(userRepository.list()).thenReturn(userList);

        //Test
        mockMvc.perform(get("/user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("regularUser","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message",is("Permission denied")));

    }

    @Test
    /*
    It tests a user trying to update info of another user.
     */
    public void testUnauthorizedUpdate() throws Exception{

        User someUserOld = someUser();
        User someUserNew = someUser();

        when(userRepository.findByUsername("user")).thenReturn(someUserOld);

        someUserNew.setRealName("updated-realname");
        someUserNew.setPassword("newPassword");
        someUserNew.setRole("updated-role");
        someUserNew.setEmail("updated-mail");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(someUserNew);
        //Test redirection
        mockMvc.perform(put("/user/{username}", "user").contentType("application/json")
                .content(json)
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("anotherUser","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message",is("Permission denied")));



    }


    @Test
    /**
     * Test that a user can view its link list
     */
    public void testAuthorizedUserLinkList() throws Exception{

        User someUser = someUser();
        when(shortURLRepository.findByCreator("user")).thenReturn(someUrlList(10));

        //Test redirection
        mockMvc.perform(get("/user/{username}/links", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(jsonPath("$.data",hasSize(10)));


    }

    @Test
    /**
     * Test that a user can not view link list of another user.
     */
    public void testUnauthorizedUserLinkList() throws Exception{

        User someUser = someUser();
        when(shortURLRepository.findByCreator("user")).thenReturn(someUrlList(10));

        //Test redirection
        mockMvc.perform(get("/user/{username}/links", "user").header("Accept", "application/json")
                //Modify the request object to include a custom Claims object. (testUser)
                .with(request -> {
                    request.setAttribute("claims",createTestUserClaims("anotherUser","user"));
                    return request;
                }))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Permission denied")));


    }


    /*
	Returns a valid Claim of user testUser and roles: user with key "secretKey".
	Used for mocking it into the controller and simulate a logged-in user.
	 */
    private Claims createTestUserClaims(String username, String roles){

        String claims =  Jwts.builder().setSubject(username)
                .claim("roles", roles).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

        return Jwts.parser().setSigningKey("secretkey")
                .parseClaimsJws(claims).getBody();
    }
}