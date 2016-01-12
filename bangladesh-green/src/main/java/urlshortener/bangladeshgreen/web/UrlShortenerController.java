package urlshortener.bangladeshgreen.web;

import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.JsonResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URIDisabledRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController

public class UrlShortenerController {
	private static final Logger log = LoggerFactory
			.getLogger(UrlShortenerController.class);

	@Value("${token.safe_browsing_key}")
	private String GOOGLE_KEY;

	private static final String availableQueue = "availableQueue";
	private static final String safeQueue = "safeQueue";
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

	@Autowired
	protected URIDisabledRepository disabledRepository;

	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected URIAvailableRepository availableRepository;

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



		ShortURL su = createAndSaveIfValid(shortURL.getTarget(), userName, extractIP(request),shortURL.isPrivateURI(), shortURL.getExpirationSeconds(),shortURL.getAuthorizedUsers());

		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());

			return new ResponseEntity<>(
					new SuccessResponse<>(su),
					h,
					HttpStatus.CREATED);
		} else {


			return new ResponseEntity<>(new ErrorResponse("Error creating ShortURL. Not valid."),HttpStatus.BAD_REQUEST);
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
											String creator, String ip, boolean isPrivate, Long expirationSeconds, List<String> authorizedUsers) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });

		//Hash is made from URL + username of creator + {true|false} (isPrivate)
		if (urlValidator.isValid(url)) {

			String baseName = url + creator + isPrivate + (expirationSeconds!=null) + (authorizedUsers!=null && authorizedUsers.size()>0);
			String id = Hashing.murmur3_32()
					.hashString(baseName, StandardCharsets.UTF_8).toString();


			// Check if the URI is available and safe
			long current = System.currentTimeMillis();
			rabbitTemplate.convertAndSend(availableQueue,url);
			long current2 = System.currentTimeMillis();
			current2 = (current2 - current);
			System.out.println(current2);

			long current3 = System.currentTimeMillis();
			rabbitTemplate.convertAndSend(safeQueue,url);
			long current4 = System.currentTimeMillis();
			current3 = (current3 - current4);
			System.out.println(current3);


			//If private, create token
			String privateToken = null;
			if(isPrivate){
				//User wants a private URL, generate random authorization token
				privateToken = Hashing.murmur3_32()
						.hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
			}
			URIAvailable a = availableRepository.findByTarget(url);
			if(a!= null) {
				if (a.isEnable()) {
					ShortURL su = new ShortURL(id, url, linkTo(
							methodOn(RedirectController.class).redirectTo(
									id, null, null, null, null)).toUri(), creator, new Date(), ip, isPrivate, privateToken, expirationSeconds, authorizedUsers);
					return shortURLRepository.save(su);
				} else {
					URIDisabled e = disabledRepository.findByTarget(url).get(0);
					URIDisabled su = new URIDisabled(id, url, linkTo(
							methodOn(RedirectController.class).redirectTo(
									id, null, null, null, null)).toUri(), creator, new Date(), ip, isPrivate, privateToken, expirationSeconds, authorizedUsers);
					disabledRepository.save(su);
					return new ShortURL(id, url, linkTo(
							methodOn(RedirectController.class).redirectTo(
									id, null, null, null, null)).toUri(), creator, new Date(), ip, isPrivate, privateToken, expirationSeconds, authorizedUsers);
				}
			}else{
				ShortURL su = new ShortURL(id, url, linkTo(
						methodOn(RedirectController.class).redirectTo(
								id, null, null, null, null)).toUri(), creator, new Date(), ip, isPrivate, privateToken, expirationSeconds, authorizedUsers);
				return shortURLRepository.save(su);

			}

		} else {
			return null;
		}

	}





}