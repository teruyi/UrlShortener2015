package urlshortener.bangladeshgreen;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
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
		WebTokenFilter authenticationFilter = new WebTokenFilter(key);


		//Protect all methods from "/link"
		URLProtection linkURL = new URLProtection("/link");
		linkURL.setAllMethods();
		authenticationFilter.addUrlToProtect(linkURL);

		//Protect GET, DELETE and PUT from "/user"
		URLProtection userURL = new URLProtection("/user");
		userURL.addMethod("GET");
		userURL.addMethod("DELETE");
		userURL.addMethod("PUT");

		authenticationFilter.addUrlToProtect(userURL);
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

	/*
	This method inititates an additional Tomcat connector on port 8080 to redirect to HTTPS 8443 port.
	 */
	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);

		return connector;
	}

}