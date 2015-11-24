package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import urlshortener.bangladeshgreen.domain.Click;

import java.util.List;

/*
 * Click repository
 */
public interface ClickRepository extends MongoRepository<Click, String> {

	public List<Click> findByHash(String hash);

	public Click findById(Long id);

	@Query("{}")
	 List<Click> list();


}
