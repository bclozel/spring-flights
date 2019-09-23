package io.spring.sample.flighttracker.profile;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProfileController {

	private final UserProfileRepository repository;

	public UserProfileController(UserProfileRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/profile/{login}")
	public Mono<UserProfile> fetchProfile(@PathVariable String login) {
		return this.repository.findByLogin(login);
	}
}
