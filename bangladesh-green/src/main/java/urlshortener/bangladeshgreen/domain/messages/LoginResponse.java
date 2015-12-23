package urlshortener.bangladeshgreen.domain.messages;

/**
 * Login response containing a token sent by server when login successfull.
 * Note: Has to be wrapped inside SuccessResponse.
 */
public class LoginResponse {

    private String token;


    public LoginResponse(){
    }

    public LoginResponse(final String token) {

        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
