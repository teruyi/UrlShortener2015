package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.User;

import java.util.List;

/**
 * User repository.
 */
public interface UserRepository extends MongoRepository<User, String> {

    public User findByUsername(String username);

    @Query("{}")
    public List<Click> list();


}
