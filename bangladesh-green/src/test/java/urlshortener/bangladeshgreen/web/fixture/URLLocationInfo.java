package urlshortener.bangladeshgreen.web.fixture;

import urlshortener.bangladeshgreen.domain.Click;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by teruyi on 2/01/16.
 */
public class URLLocationInfo {

    public static List<Click> someLocationInfo() {
        ArrayList<Click> c = new ArrayList<Click>();
        c.add(new Click("1","hash",new Date("2016/01/01"),"192.168.0.1","zaragoza","spain", "sp", "aragon","aragon"));
        c.add(new Click("2","hash",new Date("2016/01/03"),"192.168.0.1","zaragoza","spain", "sp", "aragon","aragon"));
        return  c;
    }
}
