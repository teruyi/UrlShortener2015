package urlshortener.bangladeshgreen.domain;

import java.sql.Date;

/**
 * Represents a click.
 * Author: BangladeshGreen
 */

public class Click {

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
}
