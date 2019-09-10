package io.spring.sample.radarcollector.radars;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.spring.sample.radarcollector.airports.Airport;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

@Component
public class AircraftTraceGenerator {

	private static final Duration UPDATE_INTERVAL = Duration.ofMillis(1000);

	private final LoadingCache<AirportRadar, Flux<AircraftTrace>> traces;

	public AircraftTraceGenerator() {
		this.traces = Caffeine.newBuilder()
				.maximumSize(1_000)
				.expireAfterAccess(2, TimeUnit.MINUTES)
				.build(AircraftTraceGenerator::createAircraftTrace);
	}

	private static Flux<AircraftTrace> createAircraftTrace(AirportRadar radar) {
		return Flux.interval(UPDATE_INTERVAL)
				.flatMapIterable(aLong -> {
					LocalDateTime now = LocalDateTime.now();
					Instant instant = now.withSecond((now.getSecond())).withNano(0).toInstant(ZoneOffset.UTC);
					return radar.updateTraces(instant);
				})
				.share();
	}


	public Flux<AircraftTrace> generateForAirport(Airport airport) {
		AirportRadar radar = new AirportRadar(airport);
		return this.traces.get(radar);
	}

}
