package urlshortener.bangladeshgreen.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.User;

import javax.annotation.Resource;
import java.util.List;

/**
 * User repository.
 */
@Component
public interface UserRepository extends MongoRepository<User, String> {

    public User findByUsername(String username);

    @Query("{}")
    public List<User> list();
    @Query("{'email' : ?0}")
    public User findByEmail(String mail);
    @Query("{'validationToken' : ?0}")
    public User findByValidationToken(String validationToken);


}
