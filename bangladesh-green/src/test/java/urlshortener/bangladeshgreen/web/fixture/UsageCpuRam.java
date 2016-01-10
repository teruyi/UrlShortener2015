package urlshortener.bangladeshgreen.web.fixture;

import urlshortener.bangladeshgreen.domain.UsageCpu;
import urlshortener.bangladeshgreen.domain.UsageRam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by teruyi on 10/01/16.
 */
public class UsageCpuRam {

    public static List<UsageCpu> someCpuUsage() {
        ArrayList<UsageCpu> c = new ArrayList<UsageCpu>();
        c.add(new UsageCpu(new Date("2016/01/11").getTime(),1.0));
        c.add(new UsageCpu(new Date("2016/01/10").getTime(),0.6));
        c.add(new UsageCpu(new Date("2016/01/10").getTime(),0.4));
        return  c;
    }

    public static List<UsageRam> someRamUsage() {
        ArrayList<UsageRam> c = new ArrayList<UsageRam>();
        c.add(new UsageRam(new Date("2016/01/11").getTime(),1.0));
        c.add(new UsageRam(new Date("2016/01/10").getTime(),0.6));
        c.add(new UsageRam(new Date("2016/01/10").getTime(),0.4));
        return  c;
    }
}
