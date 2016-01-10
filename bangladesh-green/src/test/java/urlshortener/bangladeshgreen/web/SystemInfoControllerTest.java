package urlshortener.bangladeshgreen.web;

/**
 * Created by teruyi on 10/01/16.
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.bangladeshgreen.repository.CPURepository;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.RamRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.web.fixture.URLLocationInfo;
import urlshortener.bangladeshgreen.web.fixture.UsageCpuRam;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.bangladeshgreen.web.fixture.ShortURLFixture.someUrlm;

/**
 * Tests for UrlInfoController, testing both Information functionality
 * request.
 */
@RunWith(MockitoJUnitRunner.class)
public class SystemInfoControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ShortURLRepository shortURLRepository;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private CPURepository cpuRepository;

    @Mock
    private RamRepository ramRepository;

    @InjectMocks
    private SystemInfoController systemInfoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(systemInfoController).build();
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and
	return cpu series average.
	 */
    public void thatReturnsJsonWithCpuAverage() throws Exception {

        when(cpuRepository.findAll()).thenReturn(UsageCpuRam.someCpuUsage());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","cpu")
                .param("day","2016/01/10")
                .param("series","average"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and return an cpu series array.
	 */
    public void thatReturnsJsonWithCpuSeries() throws Exception {

        when(cpuRepository.findAll()).thenReturn(UsageCpuRam.someCpuUsage());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","cpu")
                .param("day","2016/01/10")
                .param("series","series"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and
	return ram series average.
	 */
    public void thatReturnsJsonWithRamAverage() throws Exception {

        when(ramRepository.findAll()).thenReturn(UsageCpuRam.someRamUsage());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","ram")
                .param("day","2016/01/10")
                .param("series","average"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and return an ram series array.
	 */
    public void thatReturnsJsonWithRamSeries() throws Exception {

        when(ramRepository.findAll()).thenReturn(UsageCpuRam.someRamUsage());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","ram")
                .param("day","2016/01/10")
                .param("series","series"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and total number of clicks.
	 */
    public void thatReturnsJsonWithTotalClicks() throws Exception {

        when(clickRepository.findAll()).thenReturn(URLLocationInfo.someLocationInfo());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","clicks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and total number of clicks by day.
	 */
    public void thatReturnsJsonWithTotalDayClicks() throws Exception {

        when(clickRepository.findAll()).thenReturn(URLLocationInfo.someLocationInfo());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","clicks")
                .param("day","2016/01/01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
	/*
	Test that returns a Json Success Response with 200 (Ok request) and total number of clicks in one day by hours.
	 */
    public void thatReturnsJsonWithSeriesDayByHour() throws Exception {

        when(clickRepository.findAll()).thenReturn(URLLocationInfo.someLocationInfo());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","clicksadds")
                .param("day","2016/01/01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
	/*
	Test that returns a Json Success Response with 400 (BadRequest request).
	type = error
	 */
    public void thatReturnsJsonWithError() throws Exception {

        when(clickRepository.findAll()).thenReturn(URLLocationInfo.someLocationInfo());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
            request.setAttribute("claims",createTestUserClaims("admin","admin"));
            return request;
        })
                .param("privateToken","incorrectToken")
                .param("type","error"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
	/*
	Test that /systeminfo returns unauthorized if logged user is not admin
	 */
    public void thatSimpleInfoReturnsUnauthorizedIfAnotherUser() throws Exception {

        when(shortURLRepository.findByHash("someKey")).thenReturn(someUrlm());

        //Test that 200 Ok request is returned (Ok Request)
        mockMvc.perform(get("/systeminfo").header("Accept", "application/json").with(request -> {
                    request.setAttribute("claims",createTestUserClaims("user2","user"));
                    return request;
                })
                .param("type","error"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"));
    }

    /*
    Returns a valid Claim of user testUser and roles: user with key "secretKey".
    Used for mocking it into the controller and simulate a logged-in user.
    */
    private Claims createTestUserClaims(String username, String roles){

        String claims =  Jwts.builder().setSubject(username)
                .claim("roles", roles).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

        return Jwts.parser().setSigningKey("secretkey")
                .parseClaimsJws(claims).getBody();
    }



}
