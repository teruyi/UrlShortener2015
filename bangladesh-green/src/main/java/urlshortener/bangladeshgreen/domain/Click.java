package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Represents a click.
 * Author: BangladeshGreen
 */

public class Click {

    @Id
    private Long id;

    private String hash;
    private Date date;
    private String ip;

    public Click(Long id, String hash, Date date, String ip) {
        this.id = id;
        this.hash = hash;
        this.date = date;
        this.ip = ip;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return new String(
                "Customer[id='"+ id+"', hash='" +hash +
                        "', date='"+ date+"', ip='"+ip +"']");

    }

    public boolean compareTo(Click other){
        if(this.id.compareTo(other.id)==0){
            return true;
        }
        else {return false;}
    }
}