package urlshortener.bangladeshgreen.domain;

import java.util.Date;

/**
 * Represents a click.
 * Author: BangladeshGreen
 */

public class Click {

    private String hash;
    private Date date;
    private String ip;

    public Click(String hash, Date date, String ip) {
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
                "Customer[hash='" +hash +
                        "', date='"+ date+"', ip='"+ip +"']");

    }

}