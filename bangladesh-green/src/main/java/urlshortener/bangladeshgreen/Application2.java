package urlshortener.bangladeshgreen;

/**
 * Created by BangladeshGreen on 22/11/2015.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.repository.ClickRepository;

import java.sql.Date;

@SpringBootApplication
public class Application2 implements CommandLineRunner {

    @Autowired
    //private CustomerRepository repository;
    private ClickRepository repository2;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //repository.deleteAll();

        // save a couple of customers
        repository2.save(new Click(new Long(2), "elHash", "1-5-2015", "localhost"));
        repository2.save(new Click(new Long(3), "elHash2", "1-6-2016", "localhost"));


        System.out.println("Clicks found with findAll():");
        System.out.println("-------------------------------");
        for (Click click : repository2.findAll()) {
            System.out.println(click);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Click found with findById('2'):");
        System.out.println("--------------------------------");
        System.out.println(repository2.findById(new Long(2)));

        System.out.println("Click found with findById('3'):");
        System.out.println("--------------------------------");
        System.out.println(repository2.findById(new Long(3)));

    }

}
