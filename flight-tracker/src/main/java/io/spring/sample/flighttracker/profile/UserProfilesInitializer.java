package io.spring.sample.flighttracker.profile;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

@Component
public class UserProfilesInitializer {

	private final MongoTemplate template;

	private final MongoMappingContext mappingContext;

	public UserProfilesInitializer(MongoTemplate template, MongoMappingContext mappingContext) {
		this.template = template;
		this.mappingContext = mappingContext;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void createDefaultProfiles() {
		IndexOperations indexOps = this.template.indexOps(UserProfile.class);
		IndexResolver resolver = new MongoPersistentEntityIndexResolver(this.mappingContext);
		resolver.resolveIndexFor(UserProfile.class).forEach(indexOps::ensureIndex);

		FavoriteAirport cbg = new FavoriteAirport("CBG", "Cambridge Airport", 52.208042, 0.177738);
		UserProfile rossen = new UserProfile("rstoyanchev", "Rossen Stoyanchev", "rstoyanchev@pivotal.io",
				"https://www.gravatar.com/avatar/4b12b9c0c665bc0345467c1a218ed0f7?s=80", cbg);
		this.template.save(rossen);
		FavoriteAirport mci = new FavoriteAirport("MCI", "Kansas City International Airport", 39.293808, -94.719925);
		UserProfile rob = new UserProfile("rwinch", "Rob Winch", "rwinch@pivotal.io",
				"https://www.gravatar.com/avatar/30ed046efb35c67d4c055dab109b8933?s=80", mci);
		this.template.save(rob);
		FavoriteAirport lys = new FavoriteAirport("LYS", "Lyon Saint-Exupery Airport", 45.721424, 5.080334);
		UserProfile brian = new UserProfile("bclozel", "Brian Clozel", "bclozel@pivotal.io",
				"https://www.gravatar.com/avatar/7f6b3d65ae30e6fec52287f34e22dcb0?s=80", lys);
		this.template.save(brian);
	}
}
