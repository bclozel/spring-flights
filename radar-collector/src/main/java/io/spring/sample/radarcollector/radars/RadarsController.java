package io.spring.sample.radarcollector.radars;

import io.spring.sample.radarcollector.airports.AirportRepository;
import reactor.core.publisher.Mono;

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

}
