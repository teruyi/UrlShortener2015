package urlshortener.bangladeshgreen.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import urlshortener.bangladeshgreen.repository.UserRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.bangladeshgreen.web.fixture.UserFixture.someNotValidatedUser;
import static urlshortener.bangladeshgreen.web.fixture.UserFixture.someUser;

/**
 * Class for testing the ValidationController.
 * It tests all possible operations, requests and responses.
 * Ensures that ValidationController is working correctly.
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ValidationController controller;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    /*
     * Test that new user validation is correct if token is valid.
     */
    public void testSuccessfulValidation() throws Exception {
        // Mock userRepository response to an existing user (not validated yet)
        when(userRepository.findByValidationToken(someNotValidatedUser().getValidationToken())).thenReturn(someNotValidatedUser());

        // Do the post request to validate a new user (with valid token), and checks the results
        mockMvc.perform(get("/validation?token=" + someNotValidatedUser().getValidationToken()).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/validation.jsp"));
    }

    @Test
    /*
     * Test that new user validation is NOT correct if token has expired.
     */
    public void testExpiredTokenValidation() throws Exception {
        // Mock userRepository response to an existing user (validated)
        when(userRepository.findByValidationToken(someUser().getValidationToken())).thenReturn(someUser());

        // Do the post request to validate a new user (with used token), and checks the results
        mockMvc.perform(get("/validation?token=" + someUser().getValidationToken()).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isGone())
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/expired.jsp"));
    }

    @Test
    /*
     * Test that new user validation is NOT correct if token is invalid.
     */
    public void testInvalidTokenValidation() throws Exception {
        // Mock userRepository response to an existing user (not validated yet)
        when(userRepository.findByValidationToken(someNotValidatedUser().getValidationToken())).thenReturn(someNotValidatedUser());

        // Do the post request to validate a new user (with invalid token), and checks the results
        mockMvc.perform(get("/validation?token=" + "invalidToken").contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/validationError.jsp"));
    }
}
