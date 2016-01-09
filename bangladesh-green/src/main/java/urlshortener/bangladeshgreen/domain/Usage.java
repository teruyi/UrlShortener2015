package urlshortener.bangladeshgreen.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by teruyi on 9/01/16.
 */
public class Usage {
    @Id
    private String id;

    protected long time;
    protected double usage;

    public Usage(long time, double usage) {
        this.time = time;
        this.usage = usage;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsageRam)) return false;

        UsageRam usageCpu1 = (UsageRam) o;

        if (time != usageCpu1.time) return false;
        return Double.compare(usageCpu1.usage, usage) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (time ^ (time >>> 32));
        temp = Double.doubleToLongBits(usage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
