package urlshortener.bangladeshgreen.repository;

import urlshortener2015.common.domain.ShortURL;

import java.util.List;

public interface ShortURLRepository {

	ShortURL findByHash(String hash);

	List<ShortURL> findByTarget(String target);

	ShortURL save(ShortURL su);

	void update(ShortURL su);

	void delete(String id);

	Long count();

	List<ShortURL> list(Long limit, Long offset);

}
