package urlshortener.bangladeshgreen.web;

import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.availableQueue.Listener;
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

	@Value("${token.safe_browsing_key}")
	private String GOOGLE_KEY;

	private static final String queue = "availableQueue";
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);


	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;


	public String getGoogleKey(){
		return this.GOOGLE_KEY;
	}

	/**
	 * This method CREATES a new SHORT-LINK.
	 * */
	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public  ResponseEntity<? extends JsonResponse> createShortURL(@RequestBody final ShortURL shortURL, HttpServletRequest request) {

		logger.info("Requested new short for uri " + shortURL.getTarget());

		String userName = "anonymous";

		final Claims claims = (Claims) request.getAttribute("claims");
		userName = claims.getSubject();


		ShortURL su = createAndSaveIfValid(shortURL.getTarget(), userName, extractIP(request),shortURL.isPrivateURI());

		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());

			return new ResponseEntity<>(
					new SuccessResponse<>(su),
					h,
					HttpStatus.CREATED);
		} else {


			return new ResponseEntity<>(new ErrorResponse("Error creating ShortURL. Not valid or dead"),HttpStatus.BAD_REQUEST);
		}


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

			// Check if the URI is available and safe
			rabbitTemplate.convertSendAndReceive(queue,url);
			boolean safe = checkSafeURI(url);

			//If private, create token
			String privateToken = null;
			if(isPrivate){
				//User wants a private URL, generate random authorization token
				privateToken = Hashing.murmur3_32()
						.hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
			}


			ShortURL su = new ShortURL(id,url,	linkTo(
					methodOn(RedirectController.class).redirectTo(
							id, null,null,null,null)).toUri(),creator, new Date(),ip, isPrivate, privateToken);

			// If it's available, save the shortUrl and return it
			if (safe){
				return shortURLRepository.save(su);
			} else {
				//todo: Maybe an exception in order to diferentiate.
				return null;
			}
		} else {
			return null;
		}

	}




	protected boolean checkSafeURI(String URI){
		try{


		URL google = new
				URL("https://sb-ssl.google.com/safebrowsing/api/lookup?client=api&key="+GOOGLE_KEY+"&appver=1.5.2&pver=3.1&url="+URI);
		HttpURLConnection connection = (HttpURLConnection)google.openConnection();
		connection.setRequestMethod("GET");

		// Sets default timeout to 3 seconds
		connection.setConnectTimeout(3000);
		// Connects to the URI to check.
		connection.connect();

			Integer code2 = new Integer(connection.getResponseCode());
			String respuesta = new String(connection.getResponseMessage());

			if (code2.toString().compareTo("204")== 0){
				return true;
			} else { return false;}
		}
		catch(IOException ex){
			ex.printStackTrace();
			return false;
		}
	}

}
