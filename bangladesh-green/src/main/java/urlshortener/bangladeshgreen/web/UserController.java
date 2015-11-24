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
@RequestMapping("/user")
public class UserController {

    @Autowired
    protected UserRepository userRepository;

    public UserController() {

    }

    public UserController(UserRepository u){
        this.userRepository = u;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends JsonResponse> register(@RequestBody final User reg)
            throws ServletException {
        
        // Checks for null fields
        String check = checkRequest(reg);

        if(check == null){

            // Converts password into hash
            String hashedPassword = Hash.makeHash(reg.getPassword());
            reg.setPassword(hashedPassword);

            reg.setRole("user"); //User role

            // Checks if user exists, looking for same username or email
            User repeatedUsername = userRepository.findByUsername(reg.getUsername());
            User repeatedEmail = userRepository.findByEmail(reg.getEmail());
            if(repeatedUsername != null){
                // Username already registered
                ErrorResponse errorResponse = new ErrorResponse("User with username " + repeatedUsername.getUsername() +
                    ", is already registered.");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            } else if(repeatedEmail != null) {
                // Email already registered
                ErrorResponse errorResponse = new ErrorResponse("User with E-mail " + repeatedEmail.getEmail() +
                        ", is already registered.");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            } else {

                userRepository.save(reg);

                // User registered successfully
                reg.setPassword(null);

                SuccessResponse<User> successResponse = new SuccessResponse<>(reg);
                return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.CREATED);
            }
        } else {
            // Invalid request or invalid data provided
            ErrorResponse errorResponse = new ErrorResponse("Not a valid request. Field " + check + " not valid.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Checks this object for empty or null fields.
     * @return String with the field empty or null, otherwise null.
     */
    private String checkRequest(User user){
        if(user.getEmail()==null || user.getEmail().isEmpty()){
            return "email";
        } else if (user.getPassword()==null || user.getPassword().isEmpty()){
            return "password";
        } else if (user.getRealName()==null || user.getRealName().isEmpty()){
            return "realName";
        } else if (user.getRole()==null || user.getRole().isEmpty()){
            return "role";
        } else if(user.getUsername()==null || user.getUsername().isEmpty()){
            return "username";
        } else {
            return null;
        }
    }
}
