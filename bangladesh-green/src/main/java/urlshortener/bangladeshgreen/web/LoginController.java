package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import java.util.*;

/**
 * Controller used for user login.
 * When a user wants to log-in, they pass an object with its username and password.
 * If correct, it returns a new Token, that has to be used on every request of the client
 * as an "Authorization bearer" header.
 */
@RestController
@RequestMapping("/login")
public class LoginController {


    @Autowired
    protected UserRepository userRepository;


    private final long expirationTimeInSeconds = 3600; //One hour

    public LoginController() {

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends JsonResponse> login(@RequestBody final LoginRequest login)
            throws ServletException {


        if (login.getUsername()==null || login.getUsername().isEmpty() || login.getPassword()==null ||
                login.getPassword().isEmpty()) {
            //No user o no password provided
            ErrorResponse errorResponse = new ErrorResponse("Please, provide both user and password");
            return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
        }
        else{

            User requestedUser = userRepository.findByUsername(login.getUsername());
            // Convert password to hash for comparing
            String password = Hash.makeHash(login.getPassword());
            // Compares the requested user and both password hashes
            if(requestedUser!=null && requestedUser.getPassword().equals(password)){
                //User exists and password is correct

                //Expiration time of token
                Date expirationDate = new Date();
                expirationDate.setTime(System.currentTimeMillis() + expirationTimeInSeconds*1000);

                //All right, generate Token
                LoginResponse loginResponse = new LoginResponse(Jwts.builder().setSubject(login.getUsername())
                        .claim("roles", "user").setIssuedAt(new Date()).setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS256, "secretkey").compact());

                HttpHeaders responseHeaders = new HttpHeaders();
                return new ResponseEntity<>(
                        new SuccessResponse<>(loginResponse),
                        responseHeaders,
                        HttpStatus.OK);

            }
            else{
                //User or password incorrect
                ErrorResponse errorResponse = new ErrorResponse("User or password incorrect");
                return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
            }
        }


    }



}
