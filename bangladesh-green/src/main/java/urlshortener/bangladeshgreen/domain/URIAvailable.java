package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an object for information about URIs availability.
 * Author: BangladeshGreen
 */
public class URIAvailable {

    @Id
    private String target;
    private boolean available;
    private long date;
    private int notAvailable; // time in minutes wich uri isn't available
    private List<Integer> service;
    private List<Long> delays;
    private int times;
    private boolean change;
    private int state;
    private boolean enable;
    private String problem; // "none", "service", "delay", "down"
    public URIAvailable(String target, boolean available, long date,int state, boolean change,boolean enable, String problem) {
        this.target = target;
        this.available = available;
        this.date = date;
        this.times = 0;
        this.change = change;
        this.state = state;
        delays = new <Long> ArrayList();
        service = new <Integer> ArrayList();
        this.enable = enable;
        this.problem = problem;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getNotAvailable() {
        return notAvailable;
    }

    public void setNotAvailable(int notAvailable) {
        this.notAvailable = notAvailable;
    }

    public List<Integer> getService() {
        return service;
    }

    public void setService(List<Integer> service) {
        this.service = service;
    }

    public List<Long> getDelays() {
        return delays;
    }

    public void setDelays(List<Long> delays) {
        this.delays = delays;
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
        return new String("\nURLAvailable \n    target='"+ target + "' \n    available='" + available
                + "'\n    date='\"" + date + "\"'" +"\n    delay='\"" + delays + "\"'"
                + "\n    service='\"" + service + "\"'" +"\n    notAvailable='\""
                + notAvailable + "\"'" +"\n    state='\"" + state + "\"'"
                + "\n    enable='\"" + enable + "\n    Times='\"" + times + "\"'"
                + "\n    problem='\"" + problem + "\"'" + "\n    change='\"" + change + "\"'" + "\n");

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
