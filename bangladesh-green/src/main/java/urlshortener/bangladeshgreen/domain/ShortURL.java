package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

import java.net.URI;
import java.util.Date;

/**
 * Represents a short URL.
 * Author: BangladeshGreen
 */

public class ShortURL {

    @Id
    private String hash;

    private String target;
    private URI uri;
    private Date created;
    private String creator;
    private String ip;
    private String privateToken;
    private boolean privateURI;

    public ShortURL(String hash, String target, URI uri, String creator, Date created, String ip, boolean privateURI, String privateToken) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.creator = creator;
        this.created = created;
        this.ip = ip;
        this.privateURI = privateURI;
        this.privateToken = privateToken;
    }

    public ShortURL(){};

    public boolean isPrivateURI() {
        return privateURI;
    }

    public void setPrivateURI(boolean aPrivate) {
        privateURI = aPrivate;
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

    public String toString() {
        return new String(
                "ShortURL[hash="+ hash+ ", target='"+target
                        + "', uri='"+ uri+ "', created='"
                        + created+ "', creator='"+ creator
                        + "', ip='"+ ip+ "', privateToken='"
                        +privateToken + "']\n");

    }
    public boolean compareTo(ShortURL other){
        if(this.hash.compareTo(other.hash) == 0){
            return true;
        }
        else {return false;}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortURL shortURL = (ShortURL) o;

        if (privateURI != shortURL.privateURI) return false;
        if (!hash.equals(shortURL.hash)) return false;
        if (target != null ? !target.equals(shortURL.target) : shortURL.target != null) return false;
        if (uri != null ? !uri.equals(shortURL.uri) : shortURL.uri != null) return false;
        if (created != null ? !created.equals(shortURL.created) : shortURL.created != null) return false;
        if (creator != null ? !creator.equals(shortURL.creator) : shortURL.creator != null) return false;
        if (ip != null ? !ip.equals(shortURL.ip) : shortURL.ip != null) return false;
        return !(privateToken != null ? !privateToken.equals(shortURL.privateToken) : shortURL.privateToken != null);

    }



}
