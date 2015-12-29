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
    private String city;
    private String country;
    private String countryCode;
    private String region;
    private String regionName;

    public Click(){

    }

    public Click(String hash, Date date, String ip) {
        this.hash = hash;
        this.date = date;
        this.ip = ip;
    }

    public Click(String id, String hash, Date date, String ip, String city, String country, String countryCode,
                 String region, String regionName) {
        this.id = id;
        this.hash = hash;
        this.date = date;
        this.ip = ip;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.region = region;
        this.regionName = regionName;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String toString() {
        return "Click{" +
                "id='" + id + '\'' +
                ", hash='" + hash + '\'' +
                ", date=" + date +
                ", ip='" + ip + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", region='" + region + '\'' +
                ", regionName='" + regionName + '\'' +
                '}';
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