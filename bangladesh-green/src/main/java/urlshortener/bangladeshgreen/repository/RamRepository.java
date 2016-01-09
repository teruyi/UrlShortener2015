package urlshortener.bangladeshgreen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.UsageRam;

import java.util.List;

/*
 * Click repository
 */
@Component
public interface RamRepository extends MongoRepository<UsageRam, String> {


	@Query("{}")
	public List<UsageRam> list();

}
