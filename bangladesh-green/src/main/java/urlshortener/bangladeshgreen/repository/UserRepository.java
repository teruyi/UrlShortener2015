package urlshortener.bangladeshgreen.repository;

import urlshortener.bangladeshgreen.domain.User;

import java.util.List;

/**
 * Created by ismaro3 on 18/11/15.
 */
public interface UserRepository {

    User findByUsername(String username);

    User findByMail(String mail);

    User save(User user);

    void update(User user);

    void delete(String nick);

    Long count();

    List<User> list(Long limit, Long offset);
}
