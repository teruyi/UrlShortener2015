package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.Notify;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIDisabled;

import java.net.URI;
import java.util.List;

/*
 * Short URL repository
 */
@Component
public interface NotifyRepository extends MongoRepository<Notify, String> {


    @Query("{'target' : ?0}")
    public List<Notify> findByTarget(String target);

    @Query("{'id' : ?0}")
    public Notify findById(String id);

    @Query("{'target' : ?0, 'userName' : ?1}")
    public Notify find(String target, String userName);
}
