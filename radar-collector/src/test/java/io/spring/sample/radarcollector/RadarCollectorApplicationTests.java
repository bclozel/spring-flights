package io.spring.sample.radarcollector;

import io.spring.sample.radarcollector.radars.AirportLocation;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.rsocket.server.port=0")
class RadarCollectorApplicationTests {

	private static Logger logger = LoggerFactory.getLogger(RadarCollectorApplicationTests.class);

	@Test
	void findRadarByCode(@Autowired RSocketRequester.Builder requesterBuilder, @LocalRSocketServerPort int port) {
		Mono<RSocketRequester> requester = requesterBuilder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectTcp("localhost", port);

		Mono<AirportLocation> airport = requester.flatMap(req ->
				req.route("find.radar.{code}", "LYS")
						.retrieveMono(AirportLocation.class));

		StepVerifier.create(airport)
				.assertNext(airportLocation -> {
					logger.info("Airport found for code 'LYS': {}", airportLocation);
					assertThat(airportLocation.getCode()).isEqualTo("LYS");
					assertThat(airportLocation.getName()).isEqualTo("Lyon Saint-Exupery Airport");
				})
				.verifyComplete();
	}

}