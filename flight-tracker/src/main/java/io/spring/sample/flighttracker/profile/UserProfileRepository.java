package io.spring.sample.flighttracker.profile;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserProfileRepository extends ReactiveMongoRepository<UserProfile, String> {

	Mono<UserProfile> findByLogin(String login);

}
