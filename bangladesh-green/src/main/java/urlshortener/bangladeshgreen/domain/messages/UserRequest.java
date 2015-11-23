package urlshortener.bangladeshgreen.domain.messages;

/**
 * Register request sent by user.
 */
public class UserRequest {

    private String username;
    private String email;
    private String password;
    private String role;
    private String realName;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Checks this object for empty or null fields.
     * @return String with the field empty or null, otherwise null.
     */
    public String checkRequest(){
        if(this.email==null || this.email.isEmpty()){
            return "email";
        } else if (this.password==null || this.password.isEmpty()){
            return "password";
        } else if (this.realName==null || this.realName.isEmpty()){
            return "realName";
        } else if (this.role==null || this.role.isEmpty()){
            return "role";
        } else if(this.username==null || this.username.isEmpty()){
            return "username";
        } else {
            return null;
        }
    }

}
