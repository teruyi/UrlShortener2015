package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.messages.LoginRequest;
import urlshortener.bangladeshgreen.repository.UserRepository;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.bangladeshgreen.web.fixture.UserFixture.*;

/**
 * Test that login controller works.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setup() {
        //WebTokenFilter wtf = new WebTokenFilter("secretkey");
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        this.loginController.setKey("secretkey");
    }


    @Test
	/*
	Test that login with a correct user and password return OK and a correct token in the cookie
	 */
    public void thatLoginOKifUserAndPasswordOK()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(userRepository.findByUsername("user")).thenReturn(someUser());

        //User test = userRepository.findByUsername("user");
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        //Do the post request

        MvcResult result = mockMvc.perform(post("/login").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("success")))
                .andExpect(cookie().exists("wallaclaim"))
                .andReturn();

        //Decode content to get token
        String token = result.getResponse().getCookie("wallaclaim").getValue();

        //Decode token and check username
        Claims claims = Jwts.parser().setSigningKey("secretkey")
                .parseClaimsJws(token).getBody();


        //Check that the user inside the token is correct
        assertEquals(claims.getSubject(),"user");



    }

    @Test
	/*
	Test that login with a correct user and wrong password returns 401.
	 */
    public void thatLogin401ifUserAndPasswordWRONG()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(userRepository.findByUsername("user")).thenReturn(someUser());

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrongPassword");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        //Do the post request
        mockMvc.perform(post("/login").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("User or password incorrect")));
    }



    @Test
	/*
	Test that login with a wrong user  returns 401.
	 */
    public void thatLogin401ifUserWRONG()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(userRepository.findByUsername("user")).thenReturn(someUser());

        LoginRequest request = new LoginRequest();
        request.setUsername("wrongUser");
        request.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        //Do the post request
        mockMvc.perform(post("/login").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("User or password incorrect")));
    }


    @Test
	/*
	Test that login with no user nor password returns 401.
	 */
    public void thatLogin401ifNoUserAndNoPassword()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(userRepository.findByUsername("user")).thenReturn(someUser());

        LoginRequest request = new LoginRequest();


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        //Do the post request
        mockMvc.perform(post("/login").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Please, provide both user and password")));
    }


    @Test
	/*
	Test that login with a correct user but not validated returns 401.
	 */
    public void thatLogin401ifUserNotValidated()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(userRepository.findByUsername("user")).thenReturn(someNotValidatedUser());

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        //Do the post request
        mockMvc.perform(post("/login").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("The account has not been validated yet...")));
    }

}