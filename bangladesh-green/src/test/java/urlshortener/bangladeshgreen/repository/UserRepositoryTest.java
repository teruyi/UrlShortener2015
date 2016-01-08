package urlshortener.bangladeshgreen.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class for testing the UserRepository.
 * It tests all possible operations with the repository.
 * Ensures that UserRepository is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class UserRepositoryTest {

    private User test;
    private User test2;

    @Autowired
    private UserRepository userRepository;


    @Before
    public void setUp() throws Exception {
        // Creates new user for testing

        test = new User("test","testEmail","test","testPassword","Test UserRepository",true,"validToken");
        test2 = new User("test2","testEmail2","test2","testPassword2","Test UserRepository2",false,"validToken2");

    }


    @Test
    public void testSave() throws Exception {
        //Saves the test ShortURL
        userRepository.save(test);

        //Get the count
        long count = userRepository.count();

        assertEquals(count,1);

    }

    @Test
    //Tests that a user with the same username is not inserted twice
    public void testRepeatedSave() throws Exception {
        //Saves the test ShortURL twice
        userRepository.save(test);
        userRepository.save(test);

        //Get the count
        long count = userRepository.count();

        //Must be 1
        assertEquals(count,1);

    }

    @Test
    public void testFindByUsername() throws Exception {
        // Saves the test user
        userRepository.save(test);
        // Finds the user by username, and checks the users are the same
        User other = userRepository.findByUsername(test.getUsername());
        assertEquals(test,other);
    }

    @Test
    public void testFindByEmail() throws Exception {
        // Saves the test user
        userRepository.save(test);
        // Finds the user by email, and checks the users are the same
        User other = userRepository.findByEmail(test.getEmail());
        assertEquals(test,other);
    }

    @Test
    public void testList() throws Exception {
        // Saves the test user
        userRepository.save(test);
        // Gets all users in a list (one user)
        List<User> aux = userRepository.list();
        // Verifies the size of the list
        assertEquals(aux.size(),1);
        User other = aux.get(0);
        // Verifies the content of the list is correct
        assertEquals(test,other);
    }

    @Test
    public void testCount() throws Exception {
        userRepository.save(test);
        userRepository.save(test2);

        long count = userRepository.count();

        assertEquals(count,2);
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(test);
        test.setEmail("change");
        userRepository.save(test);
        User change = userRepository.findByUsername(test.getUsername());

        assertEquals("change",change.getEmail());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(test);

        long count = userRepository.count();
        assertEquals(count,1);
        userRepository.delete(test.getUsername());

        count = userRepository.count();
        assertEquals(count,0);

    }


    @Test
    public void testDeleteAll() throws Exception{
        // Saves the test shortURL
        userRepository.save(test);
        userRepository.save(test2);

        long count = userRepository.count();

        //Count must be two
        assertEquals(count,2);

        userRepository.deleteAll();

        count = userRepository.count();

        //Count must be zero.
        assertEquals(count,0);
    }

    @After
    //After every test, we destroy the data.
    public void finishTest() throws Exception{
        userRepository.deleteAll();
    }


}