package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ClickAdds;
import urlshortener.bangladeshgreen.domain.ShortURL;

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

	public List<ClickAdds> listByCountryBetween(Date from, Date to);

	public List<ClickAdds> listByRegionBetween(Date from, Date to);

	public List<ClickAdds> listByCity(Date from, Date to);
}
