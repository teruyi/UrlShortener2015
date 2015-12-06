package urlshortener.bangladeshgreen.web.fixture;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * Created by ismaro3 on 5/12/15.
 */
public class TokenFixture {

    public static String correctToken(){
        return Jwts.builder().setSubject("user")
                .claim("roles", "user").setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
    }

    public static String expiredToken(){

        //Expiration date is one second ago.
        Date expirationDate = new Date();
        expirationDate.setTime(System.currentTimeMillis() - 1000);

        return Jwts.builder().setSubject("user")
                .claim("roles", "user").setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").setExpiration(expirationDate).compact();

    }


    public static String badSignToken(){
        return Jwts.builder().setSubject("user")
                .claim("roles", "user").setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "badsign").compact();

    }

    public static String badFormedToken(){
        return "badToken";
    }
}
