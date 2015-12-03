package urlshortener.bangladeshgreen.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.User;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by piraces on 23/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class ShortURLRepositoryTest {

    private ShortURL test;

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Before

    //Executed before every test
    public void setUp() throws Exception {
        test = new ShortURL("someKey","http://www.google.es",null,"randomUser",new Date(),"0.0.0.0",false,null);

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
    @Ignore
    public void testFindByTarget() throws Exception{
        //TODO: Test findByTarget()

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

        //Get the count
        long count = shortURLRepository.count();

        assertEquals(count,1);


    }

    @Test
    @Ignore
    public void testUpdate() throws Exception{
        //Todo: implement
    }


    @Test
    public void testDelete() throws Exception{

        // Saves the test shortURL
        shortURLRepository.save(test);

        //Delete the previously saved shortURL
        shortURLRepository.delete(test.getHash());

        long count = shortURLRepository.count();

        //Count must be zero.
        assertEquals(count,0);


    }












}