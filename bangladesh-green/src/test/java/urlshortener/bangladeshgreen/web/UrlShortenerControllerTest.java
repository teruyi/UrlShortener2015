package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.*;

/**
 * Tests for UrlShortenerController, testing both REDIRECT functionality
 * and SHORTENER functionality.
 */
public class UrlShortenerControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@InjectMocks
	private UrlShortenerController urlShortener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
	}

	@Test
	/*
	Test that REDIRECT over a NON-PRIVATE link redirects if KEY EXISTS.
	 */
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
			throws Exception {

		//Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link redirects if KEY EXISTS
	and Private Token IS CORRECT.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenCorrect()
			throws Exception {

		//Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")
				.param("privateToken","privateToken"))
				.andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link gives error 401 if key exists
	and Private Token IS NOT CORRECT.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenIncorrect()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test that 401 Unauthorized is returned (Bad Private token)
		mockMvc.perform(get("/{id}", "someKey")
				.param("privateToken","incorrectToken"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	/*
	Test that REDIRECT over a PRIVATE link gives error 401 if key exists
	and Private Token IS NOT SUPPLIED.
	 */
	public void thatRedirectToPrivateReturnsTemporaryRedirectIfKeyExistsAndPrivateTokenNotSupplied()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		//Test that 401 Unauthorized is returned (Bad Private token)
		mockMvc.perform(get("/{id}", "someKey"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	/*
	Test that REDIRECT over an ID that does NOT EXIST gives error 404.
	 */
	public void thatRedirectToReturnsNotFoundIdIfKeyDoesNotExist()
			throws Exception {

		//Mock URLRepository to return null -> Not found
		when(shortURLRepository.findByHash("someKey")).thenReturn(null);

		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@Ignore
	//todo: REMOVE IGNORE WHEN AUTHENTICATION IS ENABLED
	/*
	Test that SHORTENER CREATES a new NON-PRIVATE redirect if the url IS OK and IS ALIVE (200 OK).

	Note: The user has to be logged-in in order to do this operation.
	We can't test here what happens if the user is not logged-in or the JWT is incorrect,
	that belongs to WebTokenFilter.
	 */
	public void thatShortenerCreatesARedirectIfTheURLisOKandIsAlive() throws Exception {
		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);


		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString("http://www.google.com/"+"testUser"+false, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims());
						return request;
					}
				})
				)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("testUser")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));
	}


	@Test
	/*
	Test that SHORTENER DOES NOT CREATE a new NON-PRIVATE redirect if the url IS OK and IS DEAD ( NOT 200 OK)
	AND RETURNS 400 BAD REQUEST.

	Note: The user has to be logged-in in order to do this operation.
	We can't test here what happens if the user is not logged-in or the JWT is incorrect,
	that belongs to WebTokenFilter.
	 */
	public void thatShortenerCreatesARedirectIfTheURLisOKandIsDead() throws Exception {
		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.welikewebengineering-eina.com/");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);



		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)

				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims());
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status",is("error")));
	}

	@Test
	/*
	Test that SHORTENER produces BAD-REQUEST if a new NON-PRIVATE redirect with WRONG URL is created.

	Note: The user has to be logged-in in order to do this operation.
	We can't test here what happens if the user is not logged-in or the JWT is incorrect,
	that belongs to WebTokenFilter.
	 */
	public void thatShortenerCreatesARedirectIfTheURLisWrong() throws Exception {
		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("notValidURL");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);


		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)

				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims());
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status",is("error")));
	}


	/*
        Test that Shortener CREATES a new PRIVATE redirect if the url IS OK and IS ALIVE.

        Note: The user has to be logged-in in order to do this operation.
        We can't test here what happens if the user is not logged-in or the JWT is incorrect,
        that belongs to WebTokenFilter.
         */
	public void thatShortenerCreatesAPrivateRedirectIfTheURLisOK() throws Exception {
		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://example.com/");
		shortURL.setPrivateURI(true);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);


		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString("http://example.com/"+"testUser"+true, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims());
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://example.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("testUser")))
				.andExpect(jsonPath("$.data.privateURI", is(true)))
				.andExpect(jsonPath("$.data.privateToken", is(notNullValue())));
	}


	@Test
	/*
	Test that Shortener produces BAD-REQUEST error if repository fails.
	 */
	public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.thenReturn(null);

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://example.com/");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);


		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims());
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status",is("error")));

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



	/*
	Returns a valid Claim of user testUser and roles: user with key "secretKey".
	Used for mocking it into the controller and simulate a logged-in user.
	 */
	private Claims createTestUserClaims(){

		String claims =  Jwts.builder().setSubject("testUser")
				.claim("roles", "user").setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();

		return Jwts.parser().setSigningKey("secretkey")
				.parseClaimsJws(claims).getBody();
	}

}
