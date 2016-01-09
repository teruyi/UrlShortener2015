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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
/**
 * Class for testing the ShortURLRepository.
 * It tests all possible operations with the repository.
 * Ensures that ShortURLRepository is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class ShortURLRepositoryTest {

    private ShortURL test;
    private ShortURL test2;

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Before
    //Executed before every test
    public void setUp() throws Exception {
        test = new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null,null,null);
        test2 = new ShortURL("someKey2","http://www.google.com",null,"randomUser",new Date(),"0.0.0.0",false,null,null,null);

    }


    @Test
    public void testSave() throws Exception {
        //Saves the test ShortURL
        shortURLRepository.save(test);

        //Get the count
        int count = (int) shortURLRepository.count();

        assertEquals(count,1);

    }


    @Test
    public void testShortURLByCreator() throws Exception {

        //First, insert some links
        shortURLRepository.save(test);
        shortURLRepository.save(test2);

        List<ShortURL> list = shortURLRepository.findByCreator("randomUser");


        assertEquals(list.size(),2);



    }


    @Test
    //Tests that a ShortURL with the same hash is not inserted
    public void testRepeatedSave() throws Exception {
        //Saves the test ShortURL twoce
        shortURLRepository.save(test);
        shortURLRepository.save(test);

        //Get the count
        long count = shortURLRepository.count();

        //Must be 1
        assertEquals(count,1);

    }

    @Test
    public void testFindByHash() throws Exception {
        //Saves the test ShortURL
        shortURLRepository.save(test);


        //Checks that the saved shortURL is intact.
        ShortURL url = shortURLRepository.findByHash(test.getHash());
        assertEquals(test,url);
    }


    @Test
    public void testFindByTarget() throws Exception{
        shortURLRepository.save(test);


        //Checks that the saved shortURL is intact.
        ShortURL url = shortURLRepository.findByTarget(test.getTarget()).get(0);
        assertEquals(test,url);

    }

    @Test
    public void testList() throws Exception {
        // Saves the test shortURL
        shortURLRepository.save(test);

        // Gets all shortURL's in a list (one user)
        List<ShortURL> aux = shortURLRepository.list();

        // Verifies the size of the list
        assertEquals(aux.size(),1);
        ShortURL other = aux.get(0);

        // Verifies the content of the list is correct
        assertEquals(test,other);
    }



    @Test
    public void testCount() throws Exception{
        // Saves the test shortURL
        shortURLRepository.save(test);
        shortURLRepository.save(test2);


        //Get the count
        long count = shortURLRepository.count();

        assertEquals(count,2);
    }

    @Test
    public void testUpdate() throws Exception{
        shortURLRepository.save(test);
        test.setTarget("change");
        shortURLRepository.save(test);

        //Get the count
        long count = shortURLRepository.count();
        ShortURL change = shortURLRepository.findByHash(test.getHash());
        assertEquals("change",change.getTarget());
    }


    @Test
    public void testDelete() throws Exception{
        // Saves the test shortURL
        shortURLRepository.save(test);

        long count = shortURLRepository.count();

        //Count must be one.
        assertEquals(count,1);

        //Delete the previously saved shortURL
        shortURLRepository.delete(test.getHash());

         count = shortURLRepository.count();

        //Count must be zero.
        assertEquals(count,0);

    }

    @Test
    public void testDeleteAll() throws Exception{
        // Saves the test shortURL
        shortURLRepository.save(test);
        shortURLRepository.save(test2);

        long count = shortURLRepository.count();

        //Count must be two
        assertEquals(count,2);

        shortURLRepository.deleteAll();

        count = shortURLRepository.count();

        //Count must be zero.
        assertEquals(count,0);

    }


    @After
    //After every test, we destroy the data.
    public void finishTest() throws Exception{
       shortURLRepository.deleteAll();
    }










}