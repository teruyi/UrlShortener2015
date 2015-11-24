package urlshortener.bangladeshgreen.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import urlshortener.bangladeshgreen.domain.ShortURL;

import java.util.List;

public interface ShortURLRepository extends MongoRepository<ShortURL, String> {

	ShortURL findByHash(String hash);

	List<ShortURL> findByTarget(String target);

	ShortURL save(ShortURL su);

	void update(ShortURL su);

	void delete(String id);

	long count();

	List<ShortURL> list(Long limit, Long offset);

}
