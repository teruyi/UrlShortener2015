package urlshortener.bangladeshgreen.web.fixture;

import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URISafe;

import java.util.Date;

/**
 * Created by Bangladesh green on 08/01/2016.
 */
public class URISafeFixture {
    public static URISafe someSafe() {
        return new URISafe("http://www.google.es",true,new Date().getTime());
    }

    public static URISafe someNotSafe(){
        return new URISafe("http://www.google.es",false,new Date().getTime());
    }

    public static URISafe someOutdated() {
        return new URISafe("http://www.google.es",true,new Date(1).getTime());    }
}
