package urlshortener.bangladeshgreen.web.fixture;

import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.secure.Hash;

/**
 * Created by ismaro3 on 5/12/15.
 */
public class UserFixture {

    public static User someUser() {
        return new User("user","user@mail.com","user", Hash.makeHash("password"),"Real name");
    }


}
