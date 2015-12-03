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
import urlshortener.bangladeshgreen.domain.User;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Class for testing the UserRepository.
 * It tests all possible operations with the repository.
 * Ensures that UserRepository is working correctly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class UserRepositoryTest {

    private User test;

    @Autowired
    private UserRepository userRepository;


    @Before
    public void setUp() throws Exception {
        // Creates new user for testing
        test = new User("test","testEmail","test","testPassword","Test UserRepository");

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

        long count = userRepository.count();

        assertEquals(count,1);
    }

    @Test
    @Ignore
    public void testUpdate() throws Exception {
        //Todo: implement
    }

    @Test
    public void testDelete() throws Exception {

        userRepository.save(test);
        userRepository.delete(test.getUsername());

        long count = userRepository.count();
        assertEquals(count,0);



    }

}