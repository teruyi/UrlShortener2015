package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.auth.WebTokenFilter;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.contains;
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
@RunWith(MockitoJUnitRunner.class)
public class WebTokenFilterTest {

    private MockMvc mockMvc;

    @Mock
    private ShortURLRepository shortURLRepository;


    @Mock
    private ClickRepository clickRespository;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @Before
    public void setup() {
        WebTokenFilter wtf = new WebTokenFilter("secretkey");
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).addFilter(wtf).build();

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

        String hashToBeGenerated = Hashing.murmur3_32()
                .hashString("http://www.google.com/"+"user"+false, StandardCharsets.UTF_8).toString();

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
        mockMvc.perform(post("/link").contentType("application/json").content(json).header("Authorization","Bearer " + "badFormatToken"))
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
        mockMvc.perform(post("/link").contentType("application/json").content(json).header("Authorization","Bearer " + expiredToken()))
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
        mockMvc.perform(post("/link").contentType("application/json").content(json).header("Authorization","Bearer " + badSignToken()))
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
        mockMvc.perform(post("/link").contentType("application/json").content(json).header("Authorization","Bearer " + correctToken()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status",is("success")));


    }



    private void configureTransparentSave() {

        when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
                .then(new Answer<ShortURL>() {
                    @Override
                    public ShortURL answer(InvocationOnMock invocation)
                            throws Throwable {
                        return (ShortURL) invocation.getArguments()[0];
                    }
                });

        when(clickRespository.save(org.mockito.Matchers.any(Click.class)))
                .then(new Answer<Click>() {
                    @Override
                    public Click answer(InvocationOnMock invocation)
                            throws Throwable {
                        return (Click) invocation.getArguments()[0];
                    }
                });
    }
}
