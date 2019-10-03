package io.spring.sample.flighttracker;

import java.net.URI;

import io.spring.sample.flighttracker.profile.UserProfile;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightTrackerApplicationTests {

	@Test
	void fetchProfileMe(@Autowired RSocketRequester.Builder builder, @LocalServerPort int port) {
		URI uri = URI.create("ws://localhost:" + port + "/rsocket");
		String login = "rossen";

		Mono<RSocketRequester> requester = builder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectWebSocket(uri);

		Mono<UserProfile> profile = requester.flatMap(req ->
				req.route("fetch.profile.me")
						.retrieveMono(UserProfile.class)
		);

		StepVerifier.create(profile)
				.assertNext(userProfile -> {
					assertThat(userProfile.getLogin()).isEqualTo(login);
				})
				.verifyComplete();
	}

}