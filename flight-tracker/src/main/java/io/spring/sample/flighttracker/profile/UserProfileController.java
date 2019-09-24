package io.spring.sample.flighttracker.profile;

import reactor.core.publisher.Mono;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserProfileController {

	private final UserProfileRepository repository;

	public UserProfileController(UserProfileRepository repository) {
		this.repository = repository;
	}

	@MessageMapping("fetch.profile.{login}")
	public Mono<UserProfile> fetchProfile(@DestinationVariable String login) {
		return this.repository.findByLogin(login);
	}
}
