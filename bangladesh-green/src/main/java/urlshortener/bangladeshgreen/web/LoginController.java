package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
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

    @Value("${token.secret_key}")
    private String key;

    @Autowired
    protected UserRepository userRepository;


    public LoginController() {

    }

    public void setKey(String key){
        this.key = key;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends JsonResponse> login(@RequestBody final LoginRequest login, HttpServletRequest request, HttpServletResponse response)
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
            if(requestedUser!=null && requestedUser.getPassword().equals(password) && requestedUser.isValidated()){
                //User exists and password is correct

                //Expiration time of token
                Date expirationDate = new Date();
                int expirationTimeInSeconds = 86400; //1 day of expiration
                expirationDate.setTime(System.currentTimeMillis() + expirationTimeInSeconds *1000);

                //All right, generate Token
                Cookie cookie = new Cookie("wallaclaim",Jwts.builder().setSubject(login.getUsername())
                        .claim("roles", requestedUser.getRole()).setIssuedAt(new Date()).setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS256, key).compact());


                cookie.setMaxAge(expirationTimeInSeconds); //Set expiration time of cookie

                response.addCookie(cookie);
                return new ResponseEntity<>(
                        new SuccessResponse<>("OK"),
                        HttpStatus.OK);

            } else if(requestedUser!=null && !requestedUser.isValidated()){
                //User or password incorrect
                ErrorResponse errorResponse = new ErrorResponse("The account has not been validated yet...");
                return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
            }
            else{
                //User or password incorrect
                ErrorResponse errorResponse = new ErrorResponse("User or password incorrect");
                return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
            }
        }


    }



}
