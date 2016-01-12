package urlshortener.bangladeshgreen.safeQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener.bangladeshgreen.TestMongoConfig;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URISafe;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URISafeRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests Available Worker and queue
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestMongoConfig.class})
public class SafeWorkerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private URISafeRepository uriSafeRepository;

    private static final String safeQueue = "safeQueue";

    @Test
    public void testHarmfulURLisDetectedAsNotSafe(){

        //Insert on Available Queue
        rabbitTemplate.convertAndSend(safeQueue,"http://malware.testing.google.test/testing/malware/");

        try{
            //Wait until worker can do it's job
            Thread.sleep(4000);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail();
        }


        //Check that URI is set as not safe
        URISafe safe = uriSafeRepository.findByTarget("http://malware.testing.google.test/testing/malware/");

        assertEquals(safe.isSafe(),false);



    }

    @Test
    public void testNotHarmfulURLisDetectedAsSafe(){

        //Insert on Available Queue
        rabbitTemplate.convertAndSend(safeQueue,"http://www.google.com");

        try{
            //Wait until worker can do it's job
            //If more than 3 seconds, the worker will timeout
            Thread.sleep(4000);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail();
        }


        //Check that URI is set as safe
        URISafe safe = uriSafeRepository.findByTarget("http://www.google.com");

        assertEquals(safe.isSafe(),true);



    }


}
