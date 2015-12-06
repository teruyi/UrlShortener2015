package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
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
				.hashString("http://www.google.com/"+"user"+false, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user"));
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
				.andExpect(jsonPath("$.data.creator", is("user")))
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
						request.setAttribute("claims",createTestUserClaims("user"));
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
						request.setAttribute("claims",createTestUserClaims("user"));
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
				.hashString("http://example.com/"+"user"+true, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user"));
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
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(true)))
				.andExpect(jsonPath("$.data.privateToken", is(notNullValue())));
	}


	@Test
	/**
	 * Checks that, the same target, shortened by two different users, produce different URIs.
	 */
	public void thatShortenerCreatesDiferentURLifDiferentUsers() throws Exception{

		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);


		String hashToBeGeneratedUser1 = Hashing.murmur3_32()
				.hashString("http://www.google.com/"+"user1"+false, StandardCharsets.UTF_8).toString();

		String hashToBeGeneratedUser2 = Hashing.murmur3_32()
				.hashString("http://www.google.com/"+"user2"+false, StandardCharsets.UTF_8).toString();

		//Do the first request (user1)
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user1"));
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGeneratedUser1)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGeneratedUser1)))
				.andExpect(jsonPath("$.data.creator", is("user1")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));


		//Do the second request. Same link, different user. (user2)
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user2"));
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGeneratedUser2)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGeneratedUser2)))
				.andExpect(jsonPath("$.data.creator", is("user2")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));


				//Check that the two hashes are different
				assertNotEquals(hashToBeGeneratedUser1,hashToBeGeneratedUser2);


	}

	@Test
	/**
	 * Checks that, if the same users creates two shortened links with the same target,
	 * but one is private and another is public, different hashes are generated.
	 */
	public void thatShortenerCreatesDiferentURLifPrivateAndPublic() throws Exception{

		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");


		ObjectMapper mapper = new ObjectMapper();



		String hashToBeGeneratedPrivate = Hashing.murmur3_32()
				.hashString("http://www.google.com/"+"user"+true, StandardCharsets.UTF_8).toString();

		String hashToBeGeneratedPublic = Hashing.murmur3_32()
				.hashString("http://www.google.com/"+"user"+false, StandardCharsets.UTF_8).toString();



		//Do the first request (private)
		shortURL.setPrivateURI(true);
		String json = mapper.writeValueAsString(shortURL);
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user"));
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGeneratedPrivate)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGeneratedPrivate)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(true)))
				.andExpect(jsonPath("$.data.privateToken", is(notNullValue())));


		//Do the second request (public)
		shortURL.setPrivateURI(false);
		json = mapper.writeValueAsString(shortURL);
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setAttribute("claims",createTestUserClaims("user"));
						return request;
					}
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status",is("success")))
				.andExpect(jsonPath("$.data.target",is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGeneratedPublic)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/"+hashToBeGeneratedPublic)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));


		//Check that the two hashes are different
		assertNotEquals(hashToBeGeneratedPublic,hashToBeGeneratedPrivate);


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
						request.setAttribute("claims",createTestUserClaims("user"));
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
	private Claims createTestUserClaims(String username){

		String claims =  Jwts.builder().setSubject(username)
				.claim("roles", "user").setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();

		return Jwts.parser().setSigningKey("secretkey")
				.parseClaimsJws(claims).getBody();
	}

}
