package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.domain.messages.*;
import urlshortener.bangladeshgreen.repository.UserRepository;
import urlshortener.bangladeshgreen.secure.Hash;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @RequestMapping(method = RequestMethod.GET)
    //User list visualization, only admin
    public ResponseEntity<? extends JsonResponse> getUserList(
            HttpServletRequest request
    ) throws ServletException {

        final Claims claims = (Claims) request.getAttribute("claims");
        String loggedUser = claims.getSubject();

        if(loggedUser.equalsIgnoreCase("admin")){

            List<User> userList = userRepository.list();
            for(User u: userList){
                u.setPassword(null);
            }

            SuccessResponse<List<User>> successResponse = new SuccessResponse<>(userList);
            return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.OK);
        }

        else{
            //Not authorized
            ErrorResponse errorResponse = new ErrorResponse("Permission denied");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }


    }


    @RequestMapping(value ="/{username}",method = RequestMethod.GET)
    //User visualization
    public ResponseEntity<? extends JsonResponse> getUserDetails(
            @PathVariable String username,HttpServletRequest request
    ) throws ServletException {


        final Claims claims = (Claims) request.getAttribute("claims");
        String loggedUser = claims.getSubject();

        //Get user from DB
        User user = userRepository.findByUsername(username);

        if(user==null){
            //User does not exist
            ErrorResponse errorResponse = new ErrorResponse("Username does not exist.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        else{
            if(loggedUser.equalsIgnoreCase(username) || loggedUser.equalsIgnoreCase("admin")){
                //User exists and is the same user or admin
                user.setPassword(null);
                SuccessResponse<User> successResponse = new SuccessResponse<>(user);
                return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.OK);
            }
            else{
                //Not authorized
                ErrorResponse errorResponse = new ErrorResponse("Permission denied");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }

    }






    //User deletion
    @RequestMapping(value ="/{username}",method = RequestMethod.DELETE)
    public ResponseEntity<? extends JsonResponse> deleteUser(
            @PathVariable String username,HttpServletRequest request
    ) throws ServletException {


        final Claims claims = (Claims) request.getAttribute("claims");
        String loggedUser = claims.getSubject();

        //Get user from DB
        User user = userRepository.findByUsername(username);

        if(user==null){
            //User does not exist
            ErrorResponse errorResponse = new ErrorResponse("Username does not exist.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        else{
            if(loggedUser.equalsIgnoreCase(username) || loggedUser.equalsIgnoreCase("admin")){
                //User exists and is the same user or admin -> can delete
                userRepository.delete(user.getUsername());
                SuccessResponse<String> successResponse = new SuccessResponse<>("User " + username + " has been deleted.");
                return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.OK);
            }
            else{
                //Not authorized
                ErrorResponse errorResponse = new ErrorResponse("Permission denied");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }

    }


    //User update (Users can change password and real name, admins can change all except username)
    @RequestMapping(value ="/{username}",method = RequestMethod.PUT)
    public ResponseEntity<? extends JsonResponse> updateUser(
            @PathVariable String username,
            @RequestBody final User updatedData,
            HttpServletRequest request
    ) throws ServletException {


        final Claims claims = (Claims) request.getAttribute("claims");
        String loggedUser = claims.getSubject();


        //Get user from DB
        User user = userRepository.findByUsername(username);




        if(user==null){
            //User does not exist
            ErrorResponse errorResponse = new ErrorResponse("Username does not exist.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        else{
            //User exists and is the same user or admin and username from object and path are the same
            if(loggedUser.equalsIgnoreCase(username) || loggedUser.equalsIgnoreCase("admin")){


                String newMail = updatedData.getEmail();
                String newPassword = updatedData.getPassword();
                String newRealName = updatedData.getRealName();
                String newRole = updatedData.getRole();

                if(loggedUser.equalsIgnoreCase("admin")){
                    //If admin, can change email and role
                    user.setEmail(newMail);
                    user.setRole(newRole);
                }

                user.setRealName(newRealName);
                //Password only changed if not null and length > 0
                if(newPassword != null && newPassword.length()>0){
                    user.setPassword(Hash.makeHash(newPassword));
                }

                userRepository.save(user);

                user.setPassword(null); //Hash is not sent
                SuccessResponse<User> successResponse = new SuccessResponse<>(user);
                return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.OK);
            }
            else{
                //Not authorized
                ErrorResponse errorResponse = new ErrorResponse("Permission denied");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
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
        } else if(user.getUsername()==null || user.getUsername().isEmpty()){
            return "username";
        } else {
            return null;
        }
    }
}
