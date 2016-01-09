package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Click;

import java.util.Date;
import java.util.List;

/*
 * Click repository
 */
@Component
public interface ClickRepository extends MongoRepository<Click, String> {

	public List<Click> findByHash(String hash);

	@Query("{}")
	List<Click> list();

	@Query("{'date' : ?0}")
	public List<Click> findByDate(Date date);

	@Query("{'ip' : ?0}")
	public List<Click> findByIP(String ip);

	@Query(value = "{ 'date' : {$gte : ?0, $lte: ?1 }}")
	public List<Click> findByDateBetween(Date start, Date end);

}
