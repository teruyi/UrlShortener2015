package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Notify;
import urlshortener.bangladeshgreen.domain.NotifyDisable;
import urlshortener.bangladeshgreen.domain.ShortURL;

import java.util.List;

/**
 * Created by Bangladesh on 13/01/2016.
 */
@Component
public interface NotifyDisableRepository extends MongoRepository<NotifyDisable, String> {


    @Query("{'target' : ?0}")
    public List<NotifyDisable> findByTarget(String target);

    @Query("{'hash' : ?0}")
    public NotifyDisable findByHash(String hash);
}
