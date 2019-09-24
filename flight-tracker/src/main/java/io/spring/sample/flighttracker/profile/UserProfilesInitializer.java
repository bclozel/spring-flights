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
		UserProfile rossen = new UserProfile("rstoyanchev", "Rossen Stoyanchev", "rstoyanchev@pivotal.io",
				"https://www.gravatar.com/avatar/4b12b9c0c665bc0345467c1a218ed0f7?s=80", "CBG");
		UserProfile rob = new UserProfile("rwinch", "Rob Winch", "rwinch@pivotal.io",
				"https://www.gravatar.com/avatar/30ed046efb35c67d4c055dab109b8933?s=80", "MCI");
		UserProfile brian = new UserProfile("bclozel", "Brian Clozel", "bclozel@pivotal.io",
				"https://www.gravatar.com/avatar/7f6b3d65ae30e6fec52287f34e22dcb0?s=80", "LYS");

		this.repository.saveAll(Arrays.asList(rossen, rob, brian)).subscribe();
	}
}
