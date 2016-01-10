package urlshortener.bangladeshgreen.web;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.domain.URISafe;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URISafeRepository;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.*;
import static urlshortener.bangladeshgreen.web.fixture.URIAvailableFixture.someAvailable;
import static urlshortener.bangladeshgreen.web.fixture.URIAvailableFixture.someNotAvailable;
import static urlshortener.bangladeshgreen.web.fixture.URIAvailableFixture.someAvailable;
import static urlshortener.bangladeshgreen.web.fixture.URISafeFixture.someNotSafe;
import static urlshortener.bangladeshgreen.web.fixture.URISafeFixture.someSafe;
import static urlshortener.bangladeshgreen.web.fixture.URISafeFixture.someOutdated;

/**
 * Tests for UrlShortenerController, testing both REDIRECT functionality
 * and SHORTENER functionality.
 */
@RunWith(MockitoJUnitRunner.class)
public class RedirectControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@Mock
	private URIAvailableRepository availableRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private URISafeRepository safeRepository;

	@InjectMocks
	private RedirectController redirectController;




	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(redirectController).build();
	}

	@Test
	/*
	Test that REDIRECT over a NON-PRIVATE link redirects if KEY EXISTS.
	 */
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
			throws Exception {

		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());

		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}


	@Test
	/*
	Test that REDIRECT over a NON-PRIVATE link redirects if KEY EXISTS,
	a EXPIRATION DATE has been set but is not expired.
	 */
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExistsAndNotExpired()
			throws Exception {

		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlWithExpirationDateButNotExpired());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}


	@Test
	/**
	 * Test that REDIRECT over a NON-PRIVATE but USER-PROTECTED redirects
	 * if user is authenticated and allowed.
	 */
	public void thatRedirectToUserProtectedRedirectsIfUserAllowed() throws Exception{
		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlWithAuthorizedUserList("user"));

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());

		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey").with(request -> {
			request.setAttribute("claims",createTestUserClaims("user"));
			return request;
		})).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));


	}


	@Test
	/**
	 * Test that REDIRECT over a NON-PRIVATE but USER-PROTECTED does not redirect (error 403)
	 * if user is authenticated and NOT allowed
	 */
	public void thatRedirectToUserProtectedReturns403IfUserNotAllowed() throws Exception{
		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlWithAuthorizedUserList("user"));

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());

		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey").with(request -> {
			request.setAttribute("claims",createTestUserClaims("anotherUser"));
			return request;
		})).andDo(print())
				.andExpect(status().isForbidden());


	}


	@Test
	/**
	 * Test that REDIRECT over a NON-PRIVATE but USER-PROTECTED redirects to BRIDGE page
	 * if user is NOT authenticated
	 */
	public void thatRedirectToUserProtectedRedirectsToBridgeIfUserNotAuthenticated() throws Exception{
		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlWithAuthorizedUserList("user"));

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());

		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

		//Test redirection
		MvcResult result = mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isUnauthorized())
				.andReturn();

		assertTrue(result.getResponse().getRedirectedUrl().contains("/bridge/someKey"));


	}



	@Test
	/*
	Test that REDIRECT over a NON-PRIVATE link DOES NOT redirect if KEY EXISTS,
	but the link IS EXPIRED.
	 */
	public void thatRedirectToDoesntReturnTemporaryRedirectIfKeyExistsAndExpired()
			throws Exception {

		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlWithExpirationDateAndExpired());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isGone());
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

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
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
	public void thatRedirectToPrivateReturns401IfKeyExistsAndPrivateTokenIncorrect()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());

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
	public void thatRedirectToPrivateReturns401IfKeyExistsAndPrivateTokenNotSupplied()
			throws Exception {

		//Mock URLrepository response to a private URL.
		when(shortURLRepository.findByHash("someKey")).thenReturn(somePrivateUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
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

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	/*
	Test that REDIRECT over an available URI.
	 */
	public void thatRedirectToAvailableURI()
			throws Exception {

		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	@Test
	/*
	Test that NO REDIRECT over a NO available URI.
	 */
	public void thatNoRedirectToNoAvailableURI()
			throws Exception {

		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someNotAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
		//Test redirection
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	/*
	* Test that not redirect over a not secure URI.
	*
	 */
	public void thatNoRedirectToNoSafeURI()
	throws Exception{
		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someNotSafe());
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().is5xxServerError());
	}
	@Test
	/*
	* Test that redirect secure URI.
	*
	 */
	public void thatRedirectToSafeURI()
			throws Exception{
		// Mock URLrepository response to someUrl.
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		// Mock URLAvailableRepository to checked URI.
		when(availableRepository.findByTarget(someUrl().getTarget())).thenReturn(someAvailable());
		// Mock URLSafeRepository to checked URI.
		when(safeRepository.findByTarget(someUrl().getTarget())).thenReturn(someSafe());
		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://www.google.es"));
	}

	/*Returns a valid Claim of user testUser and roles: user with key "secretKey".
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
