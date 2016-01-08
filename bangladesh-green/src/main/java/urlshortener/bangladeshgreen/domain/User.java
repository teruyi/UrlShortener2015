package urlshortener.bangladeshgreen.domain;
import org.springframework.data.annotation.Id;

/**
 * Represents a user.
 * Author: BangladeshGreen
 */

public class User {
    @Id
    private String username;
    private String email;
    private String password;
    private String role;
    private String realName;
    private boolean validated;
    private String validationToken;


    public User(){

    }
    public User(String username, String email, String role, String password, String realName, boolean validated, String validationToken) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.realName = realName;
        this.validated = validated;
        this.validationToken = validationToken;
    }

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

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    public String toString() {
        return new String(
                "User[username='"+username+"', email='"+email+"', password='"+password
                        +"', role='"+role+"', realName='\"+realName+\"', "
                        +"validated='"+validated+"', validationToken='"+validationToken+"']\n");

    }


    public boolean compareTo(User other){
        return this.username.compareTo(other.username) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!username.equals(user.username)) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (role != null ? !role.equals(user.role) : user.role != null) return false;
        return !(realName != null ? !realName.equals(user.realName) : user.realName != null);

    }

}
