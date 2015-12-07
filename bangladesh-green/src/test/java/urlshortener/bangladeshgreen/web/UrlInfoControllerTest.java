package urlshortener.bangladeshgreen.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.somePrivateUrl;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.someUrlm;

/**
 * Tests for UrlInfoController, testing both Information functionality
 * request.
 */
@RunWith(MockitoJUnitRunner.class)
public class UrlInfoControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ShortURLRepository shortURLRepository;

    @Mock
    private ClickRepository clickRepository;

    @InjectMocks
    private UrlInfoController urlInfoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlInfoController).build();
    }

    @Test
	/*
	 *Test that return JSON if the id exists and headers is {Accept=[application/json]}
	 */
    public void thatReturnsJsonIfIdExists()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlm());


        //Test redirection
        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.target").value("http://www.google.es"))
                .andExpect(jsonPath("$.creationDate").value(new Date().toString()))
                .andExpect(jsonPath("$.usesCount").value(0));
    }

    @Test
	/*
	Test that REDIRECT over an InfoURL ID that does NOT EXIST gives error 404.
	 */
    public void thatReturnsJsonNotFoundIdIfKeyDoesNotExist()
            throws Exception {

        //Mock URLRepository to return null -> Not found
        when(shortURLRepository.findByHash("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "application/json"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
	/*
	Test that REDIRECT over a PRIVATE InfoLink gives error 401 if key exists
	and Private Token IS NOT CORRECT.
	 */
    public void thatReturnsJsonPrivateTokenIncorrect()
            throws Exception {

        //Mock URLrepository response to a private URL.
        when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

        //Test that 401 Unauthorized is returned (Bad Private token)
        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "application/json")
                .param("privateToken","incorrectToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
	/*
	 *Test that REDIRECT over a URLInfo link redirects if id exist
	 * and headers is {Accept=[application/json]}
	 */
    public void thatReturnsTemporaryRedirectIfIdExists()
            throws Exception {

        //Mock URLrepository response to someUrl.
        when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlm());

        //Test redirection
        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "text/html"))
                .andDo(print())
                .andExpect(status().isSeeOther())
                .andExpect(forwardedUrl("info"))
                .andExpect(view().name("info"))
                .andExpect(model().attribute("target","http://www.google.es"))
                //.andExpect(model().attribute("date",new Date()))
                .andExpect(model().attribute("count",0));
    }

    @Test
	/*
	Test that REDIRECT over an InfoURL ID that does NOT EXIST gives error 404.
	 */
    public void thatRedirectToInfoURLReturnsNotFoundIdIfKeyDoesNotExist()
            throws Exception {

        //Mock URLRepository to return null -> Not found
        when(shortURLRepository.findByHash("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "text/html"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
	/*
	Test that REDIRECT over a PRIVATE InfoLink gives error 401 if key exists
	and Private Token IS NOT CORRECT.
	 */
    public void thatRedirectToPrivateInfoReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenIncorrect()
            throws Exception {

        //Mock URLrepository response to a private URL.
        when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

        //Test that 401 Unauthorized is returned (Bad Private token)
        mockMvc.perform(get("/{id}", "someKey+").header("Accept", "text/html")
                .param("privateToken","incorrectToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
