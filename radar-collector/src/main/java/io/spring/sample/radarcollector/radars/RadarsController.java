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

	public RadarsController(AirportRepository airportRepository) {
		this.airportRepository = airportRepository;
	}

	@MessageMapping("find.radar.{code}")
	public Mono<AirportLocation> findRadar(@DestinationVariable String code) {
		return this.airportRepository
				.findByCode(code.toUpperCase())
				.map(AirportLocation::new);
	}

	@MessageMapping("locate.radars.within")
	public Flux<AirportLocation> locateRadarsWithin(ViewBox box) {
		return this.airportRepository
				.findByLocationWithin(box.toGeoBox(), PageRequest.of(0, 50))
				.map(AirportLocation::new);
	}

}
