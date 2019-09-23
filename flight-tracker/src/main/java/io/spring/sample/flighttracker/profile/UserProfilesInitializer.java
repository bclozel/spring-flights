package io.spring.sample.flighttracker.profile;

import java.util.Arrays;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class UserProfilesInitializer {

	private final UserProfileRepository repository;

	public UserProfilesInitializer(UserProfileRepository repository) {
		this.repository = repository;
	}

	@PostConstruct
	public void createDefaultProfiles() {
		UserProfile rossen = new UserProfile("rstoyanchev", "Rossen Stoyanchev",
				"rstoyanchev@pivotal.io", "CBG");
		UserProfile rob = new UserProfile("rwinch", "Rob Winch",
				"rwinch@pivotal.io", "MCI");
		UserProfile brian = new UserProfile("bclozel", "Brian Clozel",
				"bclozel@pivotal.io", "LYS");

		this.repository.saveAll(Arrays.asList(rossen, rob, brian)).subscribe();
	}
}
