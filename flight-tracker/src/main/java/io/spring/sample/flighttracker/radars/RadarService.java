package io.spring.sample.flighttracker.radars;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@Component
public class RadarService {

	private final Mono<RSocketRequester> requester;

	public RadarService(RSocketRequester.Builder builder) {
		this.requester = builder
				.dataMimeType(MediaType.APPLICATION_CBOR)
				.connectTcp("localhost", 9898).retry(5).cache();
	}

	public Mono<AirportLocation> findRadar(String iata) {
		return this.requester.flatMap(req ->
				req.route(String.format("find.radar.%s", iata))
				.data(Mono.empty())
				.retrieveMono(AirportLocation.class));
	}

	public Flux<AirportLocation> findRadars(ViewBox box, int maxCount) {
		return this.requester.flatMapMany(req ->
				req.route("locate.radars.within")
						.data(box)
						.retrieveFlux(AirportLocation.class)).take(maxCount);
	}

	public Flux<AircraftSignal> streamAircraftSignals(List<Radar> radars) {
		return Flux.fromIterable(radars)
				.map(this::listenAircraftSignals)
				.collectList()
				.flatMapMany(Flux::merge);
	}

	private Flux<AircraftSignal> listenAircraftSignals(Radar radar) {
		return this.requester.flatMapMany(req ->
				req.route(String.format("listen.radar.%s", radar.getIata()))
						.data(Mono.empty())
						.retrieveFlux(AircraftSignal.class));
	}
}
