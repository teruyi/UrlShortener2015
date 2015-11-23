package urlshortener.bangladeshgreen.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.domain.messages.*;
import urlshortener.bangladeshgreen.repository.UserRepository;
import urlshortener.bangladeshgreen.secure.Hash;

import javax.servlet.ServletException;

/**
 * Controller used for user sign up.
 * When a user wants to register, they pass an object with all its user data.
 * When receiving a valid request, if user doesn't exists and sign up is correct,
 * it returns an HTTP "CREATED" response. Otherwise, an error response is sent.
 */
@RestController
@RequestMapping("/register")
public class UserController {

    @Autowired
    protected UserRepository userRepository;

    public UserController() {

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends JsonResponse> register(@RequestBody final UserRequest reg)
            throws ServletException {
        // Checks for null fields
        String check = reg.checkRequest();
        if(check == null){
            // Converts password into hash
            String password = Hash.makeHash(reg.getPassword());
            User newUser = new User(reg.getUsername(),reg.getEmail(),reg.getRole(),password,
                    reg.getRealName());
            User repeated = userRepository.findByUsername(newUser.getUsername());
            if(repeated != null){
                // User already registered
                ErrorResponse errorResponse = new ErrorResponse("User with username " + repeated.getUsername() +
                    ", is already registered.");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            } else {
                userRepository.save(newUser);
                // User registered successfully
                String username = newUser.getUsername();
                SuccessResponse<String> successResponse = new SuccessResponse<>("User " + username +
                        ", has been created " + "successfully. You can now log in.");
                return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.CREATED);
            }
        } else {
            // Invalid request or invalid data provided
            ErrorResponse errorResponse = new ErrorResponse("Not a valid request. Field " + check + " not valid.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }



}
