package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

/**
 * Represents an object for information about URIs safe.
 * Author: BangladeshGreen
 */
public class URISafe {

@Id
private String target;
private boolean safe;
private long date;

    public URISafe(String target, boolean safe, long date) {
        this.target = target;
        this.safe = safe;
        this.date = date;
    }

    public URISafe(){}


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String toString() {
        return new String("URLSafe[target='"+ target + "', safe='" + safe +
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
        URISafe other = (URISafe) o;
        if(this.safe==other.isSafe() && this.target.equals(other.target)
                && this.date==date){
            return true;
        } else {
            return false;
        }
    }
}

