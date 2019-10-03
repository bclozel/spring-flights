package io.spring.sample.flighttracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.sample.flighttracker.config.JsonMetadataStrategiesCustomizer;
import io.spring.sample.flighttracker.profile.UserProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactivePasswordTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightTrackerApplicationTests {
	@Autowired
	private RSocketRequester.Builder requesterBuilder;

	@Autowired
	private OAuth2 oauth2;

	private URI uri;

	@Test
	void fetchProfileMeWhenRossenThenRossen() {
		fetchProfileMeAndAssert("rossen");
	}

	@Test
	void fetchProfileMeWhenBrianThenBrian() {
		fetchProfileMeAndAssert("brian");
	}

	private void fetchProfileMeAndAssert(String login) {

		Mono<RSocketRequester> requester = this.requesterBuilder
				.apply(this.oauth2.tokenForLogin(login))
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectWebSocket(this.uri);

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

	@Test
	public void jwtRequired() {
		Mono<RSocketRequester> requester = this.requesterBuilder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectWebSocket(uri);

		Mono<UserProfile> profile = requester.flatMap(req ->
				req.route("fetch.profile.me")
						.retrieveMono(UserProfile.class)
		);

		StepVerifier.create(profile)
				.verifyError();
	}

	@LocalServerPort
	public void setPort(int port) {
		this.uri = URI.create("ws://localhost:" + port + "/rsocket");
	}

	@TestConfiguration
	static class OAuth2 {
		final ReactiveClientRegistrationRepository clients;

		final ObjectMapper mapper;

		public OAuth2(ReactiveClientRegistrationRepository clients, ObjectMapper mapper) {
			this.clients = clients;
			this.mapper = mapper;
		}

		public Consumer<RSocketRequester.Builder> tokenForLogin(String login) {
			return builder -> builder.setupMetadata(jsonOAuthToken(login),
					JsonMetadataStrategiesCustomizer.METADATA_MIME_TYPE);
		}

		private String jsonOAuthToken(String login) {
			Map<String, String> metadata = Collections.singletonMap(BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE.toString(), accessTokenForLogin(login));
			try {
				return this.mapper.writeValueAsString(metadata);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		private String accessTokenForLogin(String login) {
			WebClientReactivePasswordTokenResponseClient client = new WebClientReactivePasswordTokenResponseClient();
			return this.clients.findByRegistrationId("keycloak")
					.map(r -> new OAuth2PasswordGrantRequest(r, login, "password"))
					.flatMap(client::getTokenResponse)
					.map(OAuth2AccessTokenResponse::getAccessToken)
					.map(OAuth2AccessToken::getTokenValue).block();
		}
	}
}