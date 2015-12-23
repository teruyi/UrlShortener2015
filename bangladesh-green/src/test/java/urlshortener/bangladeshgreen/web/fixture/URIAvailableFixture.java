package urlshortener.bangladeshgreen.web.fixture;

import urlshortener.bangladeshgreen.domain.URIAvailable;

import java.util.Date;

/**
 * Created by piraces on 23/12/15.
 */
public class URIAvailableFixture {
    public static URIAvailable someAvailable() {
        return new URIAvailable("http://www.google.es",true,new Date().getTime());
    }

    public static URIAvailable someNotAvailable(){
        return new URIAvailable("http://www.google.es",false,new Date().getTime());
    }

    public static URIAvailable someOutdated() {
        return new URIAvailable("http://www.google.es",true,new Date(1).getTime());    }
}
