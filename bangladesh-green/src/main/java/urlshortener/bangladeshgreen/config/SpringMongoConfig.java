package urlshortener.bangladeshgreen.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import urlshortener.bangladeshgreen.repository.RepositoryPackage;
import urlshortener.bangladeshgreen.web.WebPackage;

/**
 * Created by guytili.
 */
@Configuration
@EnableMongoRepositories(basePackageClasses=RepositoryPackage.class)
@ComponentScan(basePackageClasses=WebPackage.class)
public class SpringMongoConfig extends AbstractMongoConfiguration
{
    @Value("${db.database_name}")
    private String db_name;

    @Value("${db.database_host}")
    private String db_host;

    @Value("${db.database_port}")
    private String db_port;

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception
    {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Override
    protected String getDatabaseName()
    {
        return db_name;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception
    {
        return new MongoClient(db_host , new Integer(db_port));
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception
    {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setLocation(new ClassPathResource("application.properties"));
        return c;
    }
}