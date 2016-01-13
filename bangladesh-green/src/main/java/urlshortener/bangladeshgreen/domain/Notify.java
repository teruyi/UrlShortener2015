package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by Bangladesh on 12/01/2016.
 */
public class Notify {

    @Id
    private String id;
    private String target;
    private String userName;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Notify(String target, String userName) {
        this.id = target+userName;
        this.target = target;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Notify{" +
                "id='" + id + '\'' +
                ", target='" + target + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
