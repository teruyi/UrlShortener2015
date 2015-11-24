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


    public User(){

    }
    public User(String username, String email, String role, String password, String realName) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.realName = realName;
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

    public String toString() {
        return new String(
                "User[username='"+username+"', email='"+email+"', password="+password
                        +"', role="+role+"', realName='\"+realName+\"']\n");

    }
    public boolean compareTo(User other){
        if(this.username.compareTo(other.username)==0){
            return true;
        }
        else {return false;}
    }
}
