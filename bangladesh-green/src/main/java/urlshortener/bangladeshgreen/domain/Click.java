package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Represents a click.
 * Author: BangladeshGreen
 */

public class Click {

    @Id
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return this.id.compareTo(other.id) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Click click = (Click) o;

        if (id != null ? !id.equals(click.id) : click.id != null) return false;
        if (hash != null ? !hash.equals(click.hash) : click.hash != null) return false;
        if (date != null ? !date.equals(click.date) : click.date != null) return false;
        return !(ip != null ? !ip.equals(click.ip) : click.ip != null);

    }

    @Override
    public int hashCode() {
        return 0;
    }
}