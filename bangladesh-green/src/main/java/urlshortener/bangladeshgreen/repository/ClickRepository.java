package urlshortener.bangladeshgreen.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import urlshortener.bangladeshgreen.domain.Click;

import java.util.List;

public interface ClickRepository extends MongoRepository<Click, String> {

	List<Click> findByHash(String hash);

	Click findById(Long id);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<Click> list(Long limit, Long offset);

}
