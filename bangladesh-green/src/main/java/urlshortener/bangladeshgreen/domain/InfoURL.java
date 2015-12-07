package urlshortener.bangladeshgreen.domain;

/**
 * Created by teruyi.
 */
public class InfoURL {

    private String target;
    private String creationDate;
    private int usesCount;

    public InfoURL(String target, String creationDate, int usesCount) {

        this.target = target;
        this.creationDate = creationDate;
        this.usesCount = usesCount;
    }

    public String getTarget() { return target; }

    public void setTarget(String longURI) {
        this.target = longURI;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getUsesCount() {
        return usesCount;
    }

    public void setUsesCount(int usesCount) {
        this.usesCount = usesCount;
    }

    @Override
    public String toString() {
        return "InfoURL{" +
                "longURI=" + target +
                ", creationDate=" + creationDate +
                ", usesCount=" + usesCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InfoURL)) return false;

        InfoURL infoURL = (InfoURL) o;

        if (usesCount != infoURL.usesCount) return false;
        if (target != null ? !target.equals(infoURL.target) : infoURL.target != null) return false;
        return !(creationDate != null ? !creationDate.equals(infoURL.creationDate) : infoURL.creationDate != null);

    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + usesCount;
        return result;
    }
}
