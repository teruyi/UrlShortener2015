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
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class for testing the ClickRepository.
 * It tests all possible operations with the repository.
 * Ensures that ClickRepository is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class ClickRepositoryTest {

    Click test;
    Click test2;
    @Autowired
    private ClickRepository clickRepository;
    @Before
    public void setUp() throws Exception {
        test = new Click("hash",new Date(),"localhost");
        test2 = new Click("hash2",new Date(),"localhost2");
    }


    @Test
    public void testSave() throws Exception {
        clickRepository.save(test);
        //Saves the test Click
        clickRepository.save(test);

        //Get the count
        int count = (int) clickRepository.count();

        assertEquals(count,1);

    }

    @Test
    public void testRepeatedSave() throws Exception {
        //Saves the test Click twoce
        clickRepository.save(test);
        clickRepository.save(test);

        //Get the count
        long count = clickRepository.count();

        //Must be 1
        assertEquals(count,1);
    }

    @Test
    public void testFindByHash() throws Exception {
        //Saves the test Click
        clickRepository.save(test);


        //Checks that the saved Click is intact.
        List<Click> click = clickRepository.findByHash(test.getHash());
        assertEquals(test,click.get(0));
    }

    @Test
    public void testFindByIp() throws Exception {
        //Saves the test Click
        clickRepository.save(test);


        //Checks that the saved shortURL is intact.
        List<Click> click = clickRepository.findByIP(test.getIp());
        assertEquals(test,click.get(0));
    }

    @Test
    public void testUpdate() throws Exception {
        clickRepository.save(test);
        test.setIp("change");
        clickRepository.save(test);

        //Get the count
        long count = clickRepository.count();
        List<Click> change = clickRepository.findByIP(test.getIp());
        assertEquals("change",change.get(0).getIp());
    }

    @Test
    public void testDelete() throws Exception {
        // Saves the test Click
        clickRepository.save(test);

        long count = clickRepository.count();

        //Count must be one.
        assertEquals(count, 1);

        //Delete the previously saved
        clickRepository.delete(test.getId());

        count = clickRepository.count();

        //Count must be zero.
        assertEquals(count, 0);
    }


    @Test
    public void testDeleteAll() throws Exception{
        // Saves the test Click
        clickRepository.save(test);
        clickRepository.save(test2);

        long count = clickRepository.count();

        //Count must be two
        assertEquals(count,2);

        clickRepository.deleteAll();

        count = clickRepository.count();

        //Count must be zero.
        assertEquals(count,0);

    }
    @Test
    public void testCount() throws Exception {

        clickRepository.save(test);
        clickRepository.save(test2);


        //Get the count
        long count = clickRepository.count();

        assertEquals(count,2);
    }

    @Test
    public void testList() throws Exception {
        // Saves the test Click
        clickRepository.save(test);

        // Gets all Click's in a list (one user)
        List<Click> aux = clickRepository.list();

        // Verifies the size of the list
        assertEquals(aux.size(),1);
        Click other = aux.get(0);

        // Verifies the content of the list is correct
        assertEquals(test,other);
    }

    @After
    //After every test, we destroy the data.
    public void finishTest() throws Exception{
        clickRepository.deleteAll();
    }



}