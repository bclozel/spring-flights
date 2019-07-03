package io.spring.sample.radarcollector.radars;

import io.spring.sample.radarcollector.airports.AirportRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RadarsController {

	private final AirportRepository airportRepository;

	private final AircraftTraceGenerator generator;

	public RadarsController(AirportRepository airportRepository, AircraftTraceGenerator generator) {
		this.airportRepository = airportRepository;
		this.generator = generator;
	}

	@MessageMapping("locate.radars.within")
	public Flux<AirportLocation> locateRadarsWithin(ViewBox box) {
		return this.airportRepository
				.findByLocationWithin(box.toGeoBox(), PageRequest.of(0, 50))
				.map(AirportLocation::new);
	}

	@MessageMapping("find.radar.{iata}")
	public Mono<AirportLocation> findRadar(@DestinationVariable String iata) {
		return this.airportRepository
				.findByIata(iata)
				.map(AirportLocation::new);
	}

	@MessageMapping("listen.radar.{iata}")
	public Flux<AircraftSignal> listenToRadar(@DestinationVariable String iata) {
		return this.airportRepository
				.findByIata(iata.toUpperCase())
				.flatMapMany(airport -> this.generator.aircraftTraces(new AirportRadar(airport)))
				.map(AircraftSignal::new);
	}

}
