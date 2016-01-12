package urlshortener.bangladeshgreen.availableQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests Available Worker and queue
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class AvailableWorkerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private URIAvailableRepository availableRepository;

    private static final String availableQueue = "availableQueue";
    private static final String safeQueue = "safeQueue";

    @Test
    public void testNotExistentURIisDetectedAsNotAvailable(){

        //Insert on Available Queue
        rabbitTemplate.convertAndSend(availableQueue,"http://www.asdfasfdasfasfvivaingenieriaweb.com");

        try{
            //Wait until worker can do it's job
            //If more than 3 seconds, the worker will timeout
            Thread.sleep(4000);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail();
        }


        //Check that URI is set as not available
        URIAvailable available = availableRepository.findByTarget("http://www.asdfasfdasfasfvivaingenieriaweb.com");

        assertEquals(available.isAvailable(),false);



    }

    @Test
    public void testNotServerError500URIisDetectedAsNotAvailable(){

        //Insert on Available Queue
        rabbitTemplate.convertAndSend(availableQueue,"http://httpstat.us/500");

        try{
            //Wait until worker can do it's job
            //If more than 3 seconds, the worker will timeout
            Thread.sleep(4000);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail();
        }


        //Check that URI is set as not available
        URIAvailable available = availableRepository.findByTarget("http://httpstat.us/500");

        assertEquals(available.isAvailable(),false);



    }



    @Test
    public void testExistentAndWorkingURIisDetectedAsAvailable(){

        //Insert on availableQueue
        rabbitTemplate.convertAndSend(availableQueue,"http://www.google.com");

        try{
            //Wait until worker can do it's job
            //If more than 3 seconds, the worker will timeout
            Thread.sleep(4000);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail();
        }


        //Check that URI is set as not available
        URIAvailable available = availableRepository.findByTarget("http://www.google.com");

        assertEquals(available.isAvailable(),true);



    }
}
