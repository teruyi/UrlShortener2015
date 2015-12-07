package urlshortener.bangladeshgreen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import urlshortener.bangladeshgreen.auth.WebTokenFilter;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Value("${token.secret_key}")
	private String key;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new WebTokenFilter(key));
		//Type here the URLs to protect with user authentication
		registrationBean.addUrlPatterns("/protected/*");
		registrationBean.addUrlPatterns("/link");
		return registrationBean;
	}

}