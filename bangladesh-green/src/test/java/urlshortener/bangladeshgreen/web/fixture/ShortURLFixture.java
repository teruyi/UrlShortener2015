package urlshortener.bangladeshgreen.web.fixture;


import urlshortener.bangladeshgreen.domain.ShortURL;

import java.net.URI;
import java.util.Date;

public class ShortURLFixture {

	public static ShortURL someUrl() {
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null);
	}

	public static ShortURL somePrivateUrl(){
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",true,"privateToken");
	}
}
