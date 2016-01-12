package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;

import java.util.List;

/**
 * Created by Bangladesh-green on 09/01/2016.
 */
public interface URIDisabledRepository extends MongoRepository<URIDisabled, String> {

    @Query("{'hash' : ?0}")
    public URIDisabled findByHash(String hash);

    @Query("{'target' : ?0}")
    public List<URIDisabled> findByTarget(String target);

    @Query("{}")
    public List<URIDisabled> list();

    @Query("{'creator' : ?0}")
    public List<URIDisabled> findByCreator(String creator);

    @Query("{'target' : ?0, 'creator' : ?1}")
    public List<URIDisabled> find(String target, String creator);
}