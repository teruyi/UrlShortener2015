package urlshortener.bangladeshgreen.web.fixture;


import urlshortener.bangladeshgreen.domain.ShortURL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShortURLFixture {

	public static ShortURL someUrl() {
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null,null,null);
	}

	public static ShortURL somePrivateUrl(){
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",true,"privateToken",null,null);
	}

	public static ShortURL someUrlm() {
		return new ShortURL("someKey+","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null,null,null);
	}

	public static ShortURL someUrlWithExpirationDateButNotExpired() {
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null,(long) 10000,null);
	}

	public static ShortURL someUrlWithExpirationDateAndExpired() {
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(System.currentTimeMillis() - 10000),"0.0.0.0",false,null,(long) 1,null);
	}

	public static ShortURL someUrlWithAuthorizedUserList(String authorizedUser){
		List<String> authorizedList = new ArrayList<>();
		authorizedList.add(authorizedUser);
		return new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null,null,authorizedList);

	}

	public static List<ShortURL> someUrlList(int size){
		List<ShortURL> list = new ArrayList<ShortURL>();
		for(int i = 0; i < size; i++){
			list.add(someUrl());
		}
	return list;
	}
}
