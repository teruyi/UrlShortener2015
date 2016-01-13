package urlshortener.bangladeshgreen.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
/**
 * Class for testing the URIAvailableRepository.
 * It tests all possible operations with the repository.
 * Ensures that URIAvailableRepository is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class URIAvailableRepositoryTest {

    private URIAvailable test;
    private URIAvailable test2;

    @Autowired
    private URIAvailableRepository URIRepository;

    @Before
    //Executed before every test
    public void setUp() throws Exception {
        test = new URIAvailable("http://www.google.es", true, new Date().getTime(),1,false,true,"none");
        test2 = new URIAvailable("http://www.unizar.es", false, new Date().getTime(),1,false,true,"none");

    }


    @Test
    public void testSave() throws Exception {
        //Saves the test URIAvailable
        URIRepository.save(test);

        //Get the count
        int count = (int) URIRepository.count();

        assertEquals(count, 1);

    }

    @Test
    //Tests that a URIAvailable with the same hash is not inserted
    public void testRepeatedSave() throws Exception {
        //Saves the test URIAvailable twice
        URIRepository.save(test);
        URIRepository.save(test);

        //Get the count
        long count = URIRepository.count();

        //Must be 1
        assertEquals(count, 1);

    }


    @Test
    public void testFindByTarget() throws Exception {
        URIRepository.save(test);


        //Checks that the saved URIAvailable is intact.
        URIAvailable url = URIRepository.findByTarget(test.getTarget());
        assertEquals(test, url);

    }

    @Test
    public void testList() throws Exception {
        // Saves the test URIAvailable
        URIRepository.save(test);

        // Gets all URIAvailable's in a list (one user)
        List<URIAvailable> aux = URIRepository.list();

        // Verifies the size of the list
        assertEquals(aux.size(), 1);
        URIAvailable other = aux.get(0);

        // Verifies the content of the list is correct
        assertEquals(test, other);
    }

    @Test
    public void testFindByDateLessThan() throws Exception {
        // Saves the URIAvailable tests (one modified to be outdated)
        test.setDate(0);
        URIRepository.save(test);
        URIRepository.save(test2);

        // Sets the outdated interval to one hour
        long interval = 3600*1000;
        Date now = new Date();
        now.setTime(now.getTime()-interval);
        // Gets all URIAvailable's outdated (only one)
        List<URIAvailable> aux = URIRepository.findByDateLessThan(now.getTime());

        // Verifies the size of the list
        assertEquals(aux.size(), 1);
        URIAvailable other = aux.get(0);

        // Verifies the content of the list is correct
        assertEquals(test, other);
    }


    @Test
    public void testCount() throws Exception {
        // Saves the test URIAvailable
        URIRepository.save(test);
        URIRepository.save(test2);


        //Get the count
        long count = URIRepository.count();

        assertEquals(count, 2);
    }

    @Test
    public void testUpdate() throws Exception {
        URIRepository.save(test);
        test.setTarget("change");
        URIRepository.save(test);

        //Get the count
        long count = URIRepository.count();
        URIAvailable change = URIRepository.findByTarget(test.getTarget());
        assertEquals("change", change.getTarget());
    }


    @Test
    public void testDelete() throws Exception {
        // Saves the test URIAvailable
        URIRepository.save(test);

        long count = URIRepository.count();

        //Count must be one.
        assertEquals(count, 1);

        //Delete the previously saved URIAvailable
        URIRepository.delete(test.getTarget());

        count = URIRepository.count();

        //Count must be zero.
        assertEquals(count, 0);

    }

    @Test
    public void testDeleteAll() throws Exception {
        // Saves the test URIAvailable
        URIRepository.save(test);
        URIRepository.save(test2);

        long count = URIRepository.count();

        //Count must be two
        assertEquals(count, 2);

        URIRepository.deleteAll();

        count = URIRepository.count();

        //Count must be zero.
        assertEquals(count, 0);

    }


    @After
    //After every test, we destroy the data.
    public void finishTest() throws Exception {
        URIRepository.deleteAll();
    }
}








