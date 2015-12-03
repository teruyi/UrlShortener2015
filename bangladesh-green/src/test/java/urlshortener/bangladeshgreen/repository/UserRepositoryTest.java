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
        assertEquals(sameUsers(test,other),true);
    }

    @Test
    public void testFindByEmail() throws Exception {
        // Saves the test user
        userRepository.save(test);
        // Finds the user by email, and checks the users are the same
        User other = userRepository.findByEmail(test.getEmail());
        assertEquals(sameUsers(test,other),true);
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
        assertEquals(sameUsers(test,other),true);
    }

    @After
    public void tearDown() throws Exception {
        // Deletes the test user
        userRepository.delete(test);
    }

    /**
     * Compares two Users, to prove that are equal (all their fields are equal).
     * @param one is the first User.
     * @param two is the second User.
     * @return boolean true if Users are the same, boolean false in other case.
     */
    public boolean sameUsers(User one, User two){
        if(one.getUsername().equals(two.getUsername()) && one.getRole().equals(two.getRole())
                && one.getRealName().equals(two.getRealName()) && one.getPassword().equals(two.getPassword())
                        && one.getEmail().equals(two.getEmail())){
            return true;
        } else {
            return false;
        }
    }
}