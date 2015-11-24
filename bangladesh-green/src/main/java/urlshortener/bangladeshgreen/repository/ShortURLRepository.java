package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;

import java.net.URI;
import java.util.List;

/*
 * Short URL repository
 */
public interface ShortURLRepository extends MongoRepository<ShortURL, URI> {

	public ShortURL findByHash(String hash);
	@Query("{}")
	public List<Click> list();



}
