package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Created by guytili on 09/01/2016.
 */
public class URIDisabled {
    @Id
    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String creator;
    private String ip;
    private String privateToken;
    private boolean privateURI;
    private Long expirationSeconds;
    private List<String> authorizedUsers;

    public URIDisabled(String hash, String target, URI uri, String creator, Date created,   String ip,
                       boolean privateURI,  String privateToken,   Long expirationSeconds,
                       List<String> authorizedUsers) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.creator = creator;
        this.ip = ip;
        this.privateToken = privateToken;
        this.privateURI = privateURI;
        this.expirationSeconds = expirationSeconds;
        this.authorizedUsers = authorizedUsers;
    }

    @Override
    public String toString() {
        return "URIDisabled{\n" +
                "\n   hash='" + hash + '\'' +
                "\n   target='" + target + '\'' +
                "\n   uri=" + uri +
                "\n   created=" + created +
                "\n   creator='" + creator + '\'' +
                "\n   ip='" + ip + '\'' +
                "\n   privateToken='" + privateToken + '\'' +
                "\n   privateURI=" + privateURI +
                "\n   expirationSeconds=" + expirationSeconds +
                "\n   authorizedUsers=" + authorizedUsers +
                '}';
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    public boolean isPrivateURI() {
        return privateURI;
    }

    public void setPrivateURI(boolean privateURI) {
        this.privateURI = privateURI;
    }

    public Long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(Long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public List<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(List<String> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }
}
