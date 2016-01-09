package urlshortener.bangladeshgreen;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import urlshortener.bangladeshgreen.auth.URLProtection;
import urlshortener.bangladeshgreen.auth.WebTokenFilter;
import urlshortener.bangladeshgreen.secure.Email;

@SpringBootApplication
//Wallateam
public class Application extends SpringBootServletInitializer {

	@Value("${token.secret_key}")
	private String key;


	@Value("${app.http_port}")
	private int http_port;

	@Value("${server.port}")
	private int https_port;



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
		WebTokenFilter authenticationFilter = new WebTokenFilter(key);


		//Protect all methods from "/link"
		URLProtection linkURL = new URLProtection("/link.*");
		linkURL.setAllMethods();
		authenticationFilter.addUrlToProtect(linkURL);

		//Protect GET, DELETE and PUT from "/user"
		URLProtection userURL = new URLProtection("/user.*");
		userURL.addMethod("GET");
		userURL.addMethod("DELETE");
		userURL.addMethod("PUT");
		authenticationFilter.addUrlToProtect(userURL);

		//Protect GET from aggregated link information
		URLProtection aggregatedInfoURL = new URLProtection("/info.*");
		aggregatedInfoURL.addMethod("GET");
		authenticationFilter.addUrlToProtect(aggregatedInfoURL);


		//Protect GET from simple link information
		URLProtection infoURL = new URLProtection("/.*\\+");
		infoURL.addMethod("GET");
		authenticationFilter.addUrlToProtect(infoURL);



		registrationBean.setFilter(authenticationFilter);

		return registrationBean;
	}


	@Bean
	/**
	 * This bean is used for redirecting HTTP traffic to HTTPS.
	 */
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};

		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}

	@Bean
	/**
	 * This bean is used for sending emails (for various purposes).
	 */
	public Email emailService(){
		return new Email();
	}

	/*
	This method inititates an additional Tomcat connector on port 8080 to redirect to HTTPS 8443 port.
	 */
	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(http_port);
		connector.setSecure(false);
		connector.setRedirectPort(https_port);
		return connector;
	}

}