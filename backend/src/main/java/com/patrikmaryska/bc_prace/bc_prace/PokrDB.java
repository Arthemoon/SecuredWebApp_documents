package com.patrikmaryska.bc_prace.bc_prace;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PokrDB {


	public static void main(String[] args) {
		SpringApplication.run(PokrDB.class, args);
	}

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Prague"));
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
		return (factory) -> {
			(factory)
					.addConnectorCustomizers((connector) -> {
						((AbstractHttp11Protocol<?>) connector.getProtocolHandler())
						  .setUseServerCipherSuitesOrder(true);
					});

		};
	}
}


