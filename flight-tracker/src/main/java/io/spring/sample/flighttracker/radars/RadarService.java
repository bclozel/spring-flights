package io.spring.sample.flighttracker.radars;

import java.util.List;

import org.springframework.security.rsocket.metadata.BasicAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@Component
public class RadarService {

	private final Mono<RSocketRequester> requesterMono;

	public RadarService(RSocketRequester.Builder builder) {
		this.requesterMono = builder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.setupMetadata(new UsernamePasswordMetadata("user", "password"), UsernamePasswordMetadata.BASIC_AUTHENTICATION_MIME_TYPE)
				.rsocketStrategies(s -> s.encoder(new BasicAuthenticationEncoder()))
				.connectTcp("localhost", 9898).retry(5).cache();
	}

	public Mono<AirportLocation> findRadar(String code) {
		return this.requesterMono.flatMap(req ->
				req.route("find.radar.{code}", code)
						.retrieveMono(AirportLocation.class));
	}

	public Flux<AirportLocation> findRadars(ViewBox box, int maxCount) {
		return this.requesterMono
				.flatMapMany(req ->
						req.route("locate.radars.within")
								.data(box)
								.retrieveFlux(AirportLocation.class))
				.take(maxCount);
	}

	public Flux<AircraftSignal> streamAircraftSignals(List<Radar> radars) {
		return this.requesterMono.flatMapMany(req ->
				Flux.fromIterable(radars).flatMap(radar ->
						req.route("listen.radar.{code}", radar.getCode())
								.retrieveFlux(AircraftSignal.class)));
	}
}
