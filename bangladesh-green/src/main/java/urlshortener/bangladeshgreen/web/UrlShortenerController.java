package urlshortener.bangladeshgreen.web;

import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.JsonResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.*;
import urlshortener.bangladeshgreen.domain.*;


import javax.servlet.http.HttpServletRequest;
import java.net.URI;
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
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request,@RequestParam(value="privateToken", required=false) String privateToken) {

		logger.info("Requested redirection with hash " + id + " - privateToken=" + privateToken);

		ShortURL l = shortURLRepository.findByHash(id);

		if (l != null) {
			if(l.isPrivateURI() && ( privateToken ==null || !l.getPrivateToken().equals(privateToken))){
				//If private and incorrect token, then unauthorized
				//todo: A fancy HTML?
				logger.info("Denied redirection with hash " + id + " - privateToken=" + privateToken);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
			else{
				createAndSaveClick(id, extractIP(request));
				return createSuccessfulRedirectToResponse(l);
			}
		} else {
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

	//TODO: Save clicks, needed autoincrementation of click id.
	protected void createAndSaveClick(String hash, String ip) {

		/*Click cl = new Click(,hash, new Date(),ip);
		cl=clickRepository.save(cl);
		log.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");*/
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
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();

			//TODO: if repeated, add number.

			//If private, create token
			String privateToken = null;
			if(isPrivate){
				//User wants a private URL, generate random authorization token
				privateToken = UUID.randomUUID().toString();
			}

			ShortURL su = new ShortURL(id,url,	linkTo(
					methodOn(UrlShortenerController.class).redirectTo(
							id, null,null)).toUri(),creator, new Date(),ip, isPrivate, privateToken);

			return shortURLRepository.save(su);

		} else {
			return null;
		}

	}
}
