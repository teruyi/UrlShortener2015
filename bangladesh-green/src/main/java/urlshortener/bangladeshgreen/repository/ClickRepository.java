package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Click;

import java.util.List;

/*
 * Click repository
 */
@Component
public interface ClickRepository extends MongoRepository<Click, String> {

	public List<Click> findByHash(String hash);

	@Query("{}")
	List<Click> list();


}
