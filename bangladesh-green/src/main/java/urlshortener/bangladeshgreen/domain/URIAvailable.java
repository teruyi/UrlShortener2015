package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;


/**
 * Represents an object for information about URIs availability.
 * Author: BangladeshGreen
 */
public class URIAvailable {

    @Id
    private String target;
    private boolean available;
    private long date;

    public URIAvailable(String target, boolean available, long date) {
        this.target = target;
        this.available = available;
        this.date = date;
    }

    public URIAvailable(){}


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String toString() {
        return new String("URLAvailable[target='"+ target + "', available='" + available +
                "', date='\"" + date + "\"']\n");

    }
    public boolean compareTo(ShortURL other){
        if(this.target.compareTo(other.getTarget()) == 0){
            return true;
        }
        else {return false;}
    }

    @Override
    public boolean equals(Object o) {
        URIAvailable other = (URIAvailable) o;
        if(this.available==other.isAvailable() && this.target.equals(other.target)
                && this.date==date){
            return true;
        } else {
            return false;
        }
    }
}
