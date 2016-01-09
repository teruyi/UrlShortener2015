package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.UsageCpu;

import java.util.List;

/*
 * Click repository
 */
@Component
public interface CPURepository extends MongoRepository<UsageCpu, String> {


	@Query("{}")
	public List<UsageCpu> list();


}
