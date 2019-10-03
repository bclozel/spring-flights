package io.spring.sample.radarcollector;

import java.util.List;
import java.util.regex.Pattern;

import io.spring.sample.radarcollector.radars.AircraftSignal;
import io.spring.sample.radarcollector.radars.AirportLocation;
import io.spring.sample.radarcollector.radars.LatLng;
import io.spring.sample.radarcollector.radars.ViewBox;
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

	@Autowired
	private RSocketRequester.Builder requesterBuilder;

	@LocalRSocketServerPort
	private int port;

	@Test
	void findRadarByCode() {
		Mono<RSocketRequester> requester = this.requesterBuilder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectTcp("localhost", this.port);

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

	@Test
	void findRadarWithinViewBox() {
		Mono<RSocketRequester> requester = this.requesterBuilder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectTcp("localhost", this.port);

		ViewBox viewBox = new ViewBox(new LatLng(45.9372734, 4.3803839),
				new LatLng(45.3887426, 5.5078417));

		Mono<List<AirportLocation>> airports = requester.flatMapMany(req ->
				req.route("locate.radars.within")
						.data(viewBox)
						.retrieveFlux(AirportLocation.class))
				.collectList();

		StepVerifier.create(airports)
				.assertNext(list -> {
					logger.info("Fetched airports {}", list);
					assertThat(list).extracting(AirportLocation::getCode).contains("LYS", "LYN");
				})
				.verifyComplete();
	}

	@Test
	public void streamAircraftSignalsForAirport() {
		Mono<RSocketRequester> requester = this.requesterBuilder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectTcp("localhost", this.port);

		Mono<List<AircraftSignal>> signals = requester.flatMapMany(req ->
				req.route("listen.radar.{code}", "LYS")
						.retrieveFlux(AircraftSignal.class))
				.take(20)
				.collectList();

		Pattern callSignPattern = Pattern.compile("[A-Z]{2}[0-9]{3}");

		StepVerifier.create(signals)
				.assertNext(list -> {
					logger.info("Fetched signals {}", list);
					assertThat(list).extracting(AircraftSignal::getCallSign).allMatch(callSignPattern.asPredicate());
				})
				.verifyComplete();
	}

}