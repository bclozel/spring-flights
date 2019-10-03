package io.spring.sample.flighttracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.invocation.reactive.ArgumentResolverConfigurer;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;

@SpringBootApplication
public class FlightTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightTrackerApplication.class, args);
	}

	@Bean
	RSocketMessageHandler messageHandler(RSocketStrategies rSocketStrategies) {
		RSocketMessageHandler messageHandler = new RSocketMessageHandler();
		messageHandler.setRSocketStrategies(rSocketStrategies);
		ArgumentResolverConfigurer args = messageHandler
								.getArgumentResolverConfigurer();
		args.addCustomResolver(new AuthenticationPrincipalArgumentResolver());
		return messageHandler;
	}
}