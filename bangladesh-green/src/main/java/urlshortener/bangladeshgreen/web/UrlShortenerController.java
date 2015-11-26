package urlshortener.bangladeshgreen.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.JsonResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {
	private static final Logger log = LoggerFactory
			.getLogger(UrlShortenerController.class);

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);


	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	/*
	* This method does the REDIRECT
	 */
	@RequestMapping(value = "/{id:(?!link|index|privateURL|404).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
										@RequestParam(value="privateToken", required=false) String privateToken,
										HttpServletRequest request,
										HttpServletResponse response) {

		logger.info("Requested redirection with hash " + id + " - privateToken=" + privateToken);

		ShortURL l = shortURLRepository.findByHash(id);

		if (l != null) {

			if(l.isPrivateURI() && ( privateToken ==null || !l.getPrivateToken().equals(privateToken))){
				//If private and incorrect token, then unauthorized
				//todo: Redirect to JSP "the Spring Boot way"
				logger.info("Denied redirection with hash " + id + " - privateToken=" + privateToken);

				try{
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					request.getRequestDispatcher("privateURL.jsp").forward(request, response);

				}
				catch(ServletException | IOException ex) {
				}
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

			}
			else{
				createAndSaveClick(id, extractIP(request));
				return createSuccessfulRedirectToResponse(l);
			}
		} else {
			//todo: Redirect to JSP "the Spring Boot way"

			try{
				response.setStatus(HttpStatus.NOT_FOUND.value());
				request.getRequestDispatcher("404.jsp").forward(request, response);

			}
			catch(ServletException | IOException ex) {
			}
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This method CREATES a new SHORT-LINK.
	 * */
	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public  ResponseEntity<? extends JsonResponse> createShortURL(@RequestBody final ShortURL shortURL, HttpServletRequest request) {

		logger.info("Requested new short for uri " + shortURL.getTarget());

		String user = "anonymous";

		//TODO: Uncomment for enabling authentication
		//final Claims claims = (Claims) request.getAttribute("claims");
		//user = claims.getSubject();

		//TODO: Enable authentication
		ShortURL su = createAndSaveIfValid(shortURL.getTarget(), UUID
				.randomUUID().toString(), extractIP(request),shortURL.isPrivateURI());

		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());

			return new ResponseEntity<>(
					new SuccessResponse<>(su),
					h,
					HttpStatus.CREATED);
		} else {

			return new ResponseEntity<>(new ErrorResponse("Error creating ShortURL. Not valid"),HttpStatus.BAD_REQUEST);
		}


	}

	protected void createAndSaveClick(String hash, String ip) {
		Click cl = new Click(hash, new Date(),ip);
		cl=clickRepository.save(cl);
		log.info(cl!=null?"["+hash+"] saved with date ["+cl.getDate()+"]":"["+hash+"] was not saved");
	}

	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.TEMPORARY_REDIRECT);
	}



	protected ShortURL createAndSaveIfValid(String url,
			 String creator, String ip, boolean isPrivate) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });

		//Hash is made from URL + username of creator + {true|false} (isPrivate)
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url + creator + isPrivate, StandardCharsets.UTF_8).toString();



			//If private, create token
			String privateToken = null;
			if(isPrivate){
				//User wants a private URL, generate random authorization token
				privateToken = UUID.randomUUID().toString();
			}

			
			ShortURL su = new ShortURL(id,url,	linkTo(
					methodOn(UrlShortenerController.class).redirectTo(
							id, null,null,null)).toUri(),creator, new Date(),ip, isPrivate, privateToken);

			return shortURLRepository.save(su);

		} else {
			return null;
		}

	}

	protected boolean checkURI(String URI){
		try {
			URL url = new URL(URI);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000);
			connection.connect();
			Integer code = new Integer(connection.getResponseCode());
			if(code.toString().charAt(0) == '2'){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("Warning: IOException while checking URI for short it.");
			return false;
		}
	}
}
