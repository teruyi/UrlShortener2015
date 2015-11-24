package urlshortener.bangladeshgreen.domain;

/**
 * Created by teruyi on 24/11/15.
 */
public class InfoURL {

    private String longURI;
    private String creationDate;
    private int usesCount;

    public InfoURL(String longURI, String creationDate, int usesCount) {

        this.longURI = longURI;
        this.creationDate = creationDate;
        this.usesCount = usesCount;
    }

    public String getLongURI() {
        return longURI;
    }

    public void setLongURI(String longURI) {
        this.longURI = longURI;
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
                "longURI=" + longURI +
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
        if (longURI != null ? !longURI.equals(infoURL.longURI) : infoURL.longURI != null) return false;
        return !(creationDate != null ? !creationDate.equals(infoURL.creationDate) : infoURL.creationDate != null);

    }

    @Override
    public int hashCode() {
        int result = longURI != null ? longURI.hashCode() : 0;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + usesCount;
        return result;
    }
}
