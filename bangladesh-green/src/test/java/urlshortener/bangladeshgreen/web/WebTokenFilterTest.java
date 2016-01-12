package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.auth.URLProtection;
import urlshortener.bangladeshgreen.auth.WebTokenFilter;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.bangladeshgreen.web.fixture.TokenFixture.*;

/**
 * This class tests the Web Token Filter, responsible of checking the authorization token,
 * and deny or allow access to controllers based on that.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers=ConfigFileApplicationContextInitializer.class)
public class WebTokenFilterTest {

    private MockMvc mockMvc;

    @Mock
    private ShortURLRepository shortURLRepository;


    private String GOOGLE_KEY;

    @Mock
    private ClickRepository clickRespository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UrlShortenerController urlShortener;
    @Mock
    private URIAvailableRepository uriAvailableRepository;
    @Autowired
    private ConfigurableApplicationContext c;

    @Before
    public void setup() {
        WebTokenFilter wtf = new WebTokenFilter("secretkey");

        //Protect all methods from "/link"
        URLProtection linkURL = new URLProtection("/link");
        linkURL.setAllMethods();
        wtf.addUrlToProtect(linkURL);

        //Protect GET, DELETE and PUT from "/user"
        URLProtection userURL = new URLProtection("/user");
        userURL.addMethod("GET");
        userURL.addMethod("DELETE");
        userURL.addMethod("PUT");

        wtf.addUrlToProtect(userURL);

        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).addFilter(wtf).build();

        //Get GOOGLE_KEY from properties and set it on urlShortener
        GOOGLE_KEY =  c.getEnvironment().getProperty("token.safe_browsing_key");
        ReflectionTestUtils.setField(urlShortener,"GOOGLE_KEY", GOOGLE_KEY);

    }

    @Test
	/*
	Test that, if we want to do a protected operation (Create a shorted link),
	it returns 401 NOT AUTHORIZED if NO TOKEN IS SUPPLIED.
	 */
    public void thatReturns401ifNoTokenIsSupplied()
            throws Exception {

        //Create URL
        ShortURL shortURL = new ShortURL();
        shortURL.setTarget("http://www.google.com/");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(shortURL);


        //Do the post request
        mockMvc.perform(post("/link").contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Authorization error: No token is supplied. Please obtain one from /login.")));


    }

    @Test
	/*
	Test that, if we want to do a protected operation (Create a shorted link),
	it returns 401 NOT AUTHORIZED if A BAD FORMED TOKEN IS SUPPLIED.
	 */
    public void thatReturns401ifBadFormedTokenIsSupplied()
            throws Exception {

        //Create URL
        ShortURL shortURL = new ShortURL();
        shortURL.setTarget("http://www.google.com/");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(shortURL);

        //Do the post request
        Cookie cookie = new Cookie("wallaclaim","badFormatToken");
        mockMvc.perform(post("/link").contentType("application/json").content(json).cookie(cookie))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Authorization error: Invalid token format. Please obtain a new token from /login")));


    }

    @Test
	/*
	Test that, if we want to do a protected operation (Create a shorted link),
	it returns 401 NOT AUTHORIZED if AN EXPIRED TOKEN is supplied
	 */
    public void thatReturns401ifExpiredTokenIsSupplied()
            throws Exception {

        //Create URL
        ShortURL shortURL = new ShortURL();
        shortURL.setTarget("http://www.google.com/");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(shortURL);


        //Do the post request
        Cookie cookie = new Cookie("wallaclaim",expiredToken());
        mockMvc.perform(post("/link").contentType("application/json").content(json).cookie(cookie))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",containsString("Authorization error: JWT expired")));


    }


    @Test
	/*
	Test that, if we want to do a protected operation (Create a shorted link),
	it returns 401 NOT AUTHORIZED if a token with A BAD SIGN is supplied.
	 */
    public void thatReturns401ifBadSignIsSupplied()
            throws Exception {

        //Create URL
        ShortURL shortURL = new ShortURL();
        shortURL.setTarget("http://www.google.com/");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(shortURL);

        //Do the post request
        Cookie cookie = new Cookie("wallaclaim",badSignToken());
        mockMvc.perform(post("/link").contentType("application/json").content(json).cookie(cookie))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status",is("error")))
                .andExpect(jsonPath("$.message",is("Authorization error: Invalid token format. Please obtain a new token from /login")));


    }


    @Test
	/*
	Test that, if we want to do a protected operation (Create a shorted link),
	it returns 201 CREATED (Operation is done) if a correct token is supplied.
	 */
    public void thatReturns200ifValidTokenIsSupplied()
            throws Exception {
        configureTransparentSave();

        //Create URL
        ShortURL shortURL = new ShortURL();
        shortURL.setTarget("http://www.google.com/");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(shortURL);


        //Do the post request
        Cookie cookie = new Cookie("wallaclaim",correctToken());
        mockMvc.perform(post("/link").contentType("application/json").content(json).cookie(cookie))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status",is("success")));


    }



    private void configureTransparentSave() {

        when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
                .then(invocation -> invocation.getArguments()[0]);

        when(clickRespository.save(org.mockito.Matchers.any(Click.class)))
                .then(invocation -> invocation.getArguments()[0]);
    }
}
