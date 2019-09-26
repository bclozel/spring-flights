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

	@MessageMapping("fetch.profile.me")
	public Mono<UserProfile> fetchProfile() {
		// hardcoded username until authentication is here!
		return this.repository.findByLogin("rossen");
	}

	@MessageMapping("fetch.profile.{login}")
	public Mono<PublicUserProfile> fetchPublicProfile(@DestinationVariable String login) {
		return this.repository.findByLogin(login).map(PublicUserProfile::new);
	}
}
