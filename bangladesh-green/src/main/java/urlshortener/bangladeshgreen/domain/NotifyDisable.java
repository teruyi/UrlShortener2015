package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by guytili on 13/01/2016.
 */
public class NotifyDisable {
    @Id
    private String hash;
    private String target;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public NotifyDisable(String hash, String target) {
        this.hash = hash;
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "NotifyDisable{" +
                "hash='" + hash + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
