package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;

import java.util.List;

/*
 * URIAvailable repository
 */
@Component
public interface URIAvailableRepository extends MongoRepository<URIAvailable, String> {

    @Query("{'target' : ?0}")
    public URIAvailable findByTarget(String target);

    @Query("{}")
    public List<URIAvailable> list();

    // Todo: implement this method
    // @Query("{'date' : ?0}")
    // public List<URIAvailable> listUnchecked();



}
