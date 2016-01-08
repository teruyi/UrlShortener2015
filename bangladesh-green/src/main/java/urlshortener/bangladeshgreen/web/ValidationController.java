package urlshortener.bangladeshgreen.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Map;

/**
 * Controller used for user validation (through email with unique token).
 */
@Controller
public class ValidationController {

    @Autowired
    protected UserRepository userRepository;


    public ValidationController() {

    }

    public ValidationController(UserRepository u){
        this.userRepository = u;
    }

    @RequestMapping(value = "/validation", method = RequestMethod.GET)
    /**
     * Validates a recently registered user, allowing the user to login and use the services provided.
     */
    public Object register(@RequestParam(value="token", required=true) String token,
                                                           HttpServletResponse response, HttpServletRequest request,
                                                           Map<String, Object> model) throws ServletException {

        // Finds the user by validationToken in the user repository
        User searched = userRepository.findByValidationToken(token);
        if(searched!=null){
            // Valid user founded
            if(searched.isValidated()){
                // Validation token expired
                response.setStatus(HttpStatus.GONE.value());
                return "expired";
            } else if(!searched.isValidated() && token.equals(searched.getValidationToken())){
                // Valid user validation and token (all correct)
                searched.setValidated(true);
                // Saves the new state of user
                userRepository.save(searched);
                // Response successful request
                response.setStatus(HttpStatus.ACCEPTED.value());
                URI contextUrl = URI.create(request.getRequestURL().toString()).resolve(request.getContextPath());
                model.put("url",contextUrl);
                model.put("user",searched.getUsername());
                model.put("email",searched.getEmail());
                return "validation";
            } else {
                // Invalid parameters of user validation
                response.setStatus(HttpStatus.FORBIDDEN.value());
                model.put("token",token);
                return "validationError";
            }
        } else {
            // Invalid User for validation
            response.setStatus(HttpStatus.FORBIDDEN.value());
            model.put("token",token);
            return "validationError";
        }
    }
}
