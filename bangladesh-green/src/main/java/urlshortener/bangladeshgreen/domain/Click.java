package urlshortener.bangladeshgreen.domain;
import java.sql.Date;
import org.springframework.data.annotation.Id;
/**
 * Represents a click.
 * Author: BangladeshGreen
 */

public class Click {

    @Id
    private Long id;

    private String hash;
    private String date;
    private String ip;

    public Click(Long id, String hash, String date, String ip) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
        return String.format(
                "Customer[id=%d, hash='%s', date='%s', ip='%s']",
                id, hash, date, ip);

    }
}