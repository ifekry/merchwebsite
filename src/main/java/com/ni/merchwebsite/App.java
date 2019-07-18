package com.ni.merchwebsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class App extends SpringBootServletInitializer
{
	
	private static final Logger log = LoggerFactory.getLogger(App.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(App.class);
	}
	
	/* Application entry point */
    public static void main( String[] args )
    {
    	log.debug("Starting Application");
    	System.setProperty("jasypt.encryptor.password", "P@ssw0rd#900"); /* Configuration File Password */
    	SpringApplication.run(App.class, args);
    	log.info("Application Started");
    }
}
