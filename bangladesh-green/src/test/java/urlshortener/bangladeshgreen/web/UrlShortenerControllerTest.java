package urlshortener.bangladeshgreen.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for UrlShortenerController, testing both REDIRECT functionality
 * and SHORTENER functionality.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers=ConfigFileApplicationContextInitializer.class)
public class UrlShortenerControllerTest {

	private MockMvc mockMvc;


	private String GOOGLE_KEY;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private UrlShortenerController urlShortener;

	@Autowired
	private ConfigurableApplicationContext c;

	@Mock
	private URIAvailableRepository uriAvailableRepository;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();

		//Get GOOGLE_KEY from properties and set it on urlShortener
		GOOGLE_KEY =  c.getEnvironment().getProperty("token.safe_browsing_key");
		ReflectionTestUtils.setField(urlShortener,"GOOGLE_KEY", GOOGLE_KEY);


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


		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseName = "http://www.google.com/" + "user" + false + false + false;
		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString(baseName, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
					request.setAttribute("claims", createTestUserClaims("user"));
					return request;
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status", is("success")))
				.andExpect(jsonPath("$.data.target", is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/" + hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));
	}


	@Test
	/*
	Test that SHORTENER CREATES a new regular (NON-PRIVATE) redirect with EXPIRATION time
	(if it is alive and not harmful).
	 */
	public void thatShortenerCreatesARedirectWithExpirationIfTheURLisOKandIsAlive() throws Exception {

		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");

		Random secondsGenerator = new Random();
		long seconds = secondsGenerator.nextLong()*9999;

		shortURL.setExpirationSeconds(seconds);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);

		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseName = "http://www.google.com/" + "user" + false + true + false;

		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString(baseName, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
					request.setAttribute("claims", createTestUserClaims("user"));
					return request;
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status", is("success")))
				.andExpect(jsonPath("$.data.target", is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/" + hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())))
				.andExpect(jsonPath("$.data.expirationSeconds").value(seconds));
	}

	@Test
	/*
	Test that SHORTENER CREATES a new regular (NON-PRIVATE) redirect with AUTHORIZED USER LIST
	(if it is alive and not harmful).
	 */
	public void thatShortenerCreatesARedirectWithUserListIfTheURLisOKandIsAlive() throws Exception {

		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");

		ArrayList<String> users = new ArrayList<>();
		users.add("ismaro3");
		users.add("pepito");
		users.add("alberto");

		shortURL.setAuthorizedUsers(users);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);



		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseName = "http://www.google.com/" + "user" + false + false + true;
		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString(baseName, StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
					request.setAttribute("claims", createTestUserClaims("user"));
					return request;
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status", is("success")))
				.andExpect(jsonPath("$.data.target", is("http://www.google.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/" + hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())))
				.andExpect(jsonPath("$.data.authorizedUsers", Matchers.containsInAnyOrder("ismaro3","pepito","alberto")));
	}


	@Test

	/*
	Test that SHORTENER DOES NOT CREATE a new NON-PRIVATE redirect if the url IS OK and IS DEAD ( NOT 200 OK)
	AND RETURNS SUCCESS.

	Note: The user has to be logged-in in order to do this operation.
	We can't test here what happens if the user is not logged-in or the JWT is incorrect,
	that belongs to WebTokenFilter.
	 */
	public void thatShortenerCreateARedirectIfTheURLisOKandIsDead() throws Exception {
		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.welikewebengineering-eina.com/");



		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(shortURL);

		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseName = "http://www.welikewebengineering-eina.com/" + "user" + false + false + false;

		String hashToBeGenerated = Hashing.murmur3_32()
				.hashString(baseName,
						StandardCharsets.UTF_8).toString();

		//Do the post request
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
					request.setAttribute("claims", createTestUserClaims("user"));
					return request;
				})
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status", is("success")))
				.andExpect(jsonPath("$.data.target", is("http://www.welikewebengineering-eina.com/")))
				.andExpect(jsonPath("$.data.hash", is(hashToBeGenerated)))
				.andExpect(jsonPath("$.data.uri", is("http://localhost/" + hashToBeGenerated)))
				.andExpect(jsonPath("$.data.creator", is("user")))
				.andExpect(jsonPath("$.data.privateURI", is(false)))
				.andExpect(jsonPath("$.data.privateToken", is(nullValue())));
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
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user"));
                    return request;
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
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user"));
                    return request;
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


		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseNameUser1 = "http://www.google.com/" + "user1" + false + false + false;
		String hashToBeGeneratedUser1 = Hashing.murmur3_32()
				.hashString(baseNameUser1, StandardCharsets.UTF_8).toString();

		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseNameUser2 = "http://www.google.com/" + "user2" + false + false + false;
		String hashToBeGeneratedUser2 = Hashing.murmur3_32()
				.hashString(baseNameUser2, StandardCharsets.UTF_8).toString();

		//Do the first request (user1)
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user1"));
                    return request;
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
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user2"));
                    return request;
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
	public void thatShortenerCreatesDifferentURLifDifferentModes() throws Exception{

		configureTransparentSave();

		//Create URL
		ShortURL shortURL = new ShortURL();
		shortURL.setTarget("http://www.google.com/");


		ObjectMapper mapper = new ObjectMapper();


		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseNamePrivate = "http://www.google.com/" + "user" + true + false + false;

		String hashToBeGeneratedPrivate = Hashing.murmur3_32()
				.hashString(baseNamePrivate, StandardCharsets.UTF_8).toString();


		//Name: URL + user + hasPrivateToken + hasExpirationDate + isUserRestricted
		String baseNamePublic = "http://www.google.com/" + "user" + false + false + false;

		String hashToBeGeneratedPublic = Hashing.murmur3_32()
				.hashString(baseNamePublic, StandardCharsets.UTF_8).toString();



		//Do the first request (private)
		shortURL.setPrivateURI(true);
		String json = mapper.writeValueAsString(shortURL);
		mockMvc.perform(post("/link").contentType("application/json").content(json)
				//Modify the request object to include a custom Claims object. (testUser)
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user"));
                    return request;
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
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user"));
                    return request;
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
				//Modify the request object to include a custom Claims object.
				.with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user"));
                    return request;
                })
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status",is("error")));

	}


	private void configureTransparentSave() {

		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.then(invocation -> invocation.getArguments()[0]);

		when(clickRespository.save(org.mockito.Matchers.any(Click.class)))
				.then(invocation -> invocation.getArguments()[0]);
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
