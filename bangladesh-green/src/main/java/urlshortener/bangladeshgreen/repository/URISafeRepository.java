package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import urlshortener.bangladeshgreen.domain.URISafe;

import java.util.List;

/**
 * Created Bangladesh-green
 * URISafe Repository
 */
public interface URISafeRepository extends MongoRepository<URISafe, String> {

    @Query("{'target' : ?0}")
    public URISafe findByTarget(String target);

    public List<URISafe> findByDateLessThan(long date);
    @Query("{}")
    public List<URISafe> list();


}
