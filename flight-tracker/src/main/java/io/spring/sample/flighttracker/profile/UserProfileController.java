package io.spring.sample.flighttracker.profile;

import io.spring.sample.flighttracker.CurrentUserProfile;
import org.springframework.security.core.annotation.CurrentSecurityContext;
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
	public Mono<UserProfile> fetchProfile(@CurrentUserProfile Mono<UserProfile> currentUserProfile) {
		return currentUserProfile;
	}

	@MessageMapping("fetch.profile.{login}")
	public Mono<PublicUserProfile> fetchPublicProfile(@DestinationVariable String login) {
		return this.repository.findByLogin(login).map(PublicUserProfile::new);
	}
}
