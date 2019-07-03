package io.spring.sample.radarcollector.radars;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

@Component
public class AircraftTraceGenerator {

	private final Duration updateInterval;

	private final LoadingCache<AirportRadar, Flux<AircraftTrace>> traces;

	public AircraftTraceGenerator() {
		this.updateInterval = Duration.ofMillis(1000);
		this.traces = Caffeine.newBuilder()
				.maximumSize(1_000)
				.expireAfterAccess(2, TimeUnit.MINUTES)
				.build(this::createAircraftTrace);
	}


	public Flux<AircraftTrace> aircraftTraces(AirportRadar radar) {
		return this.traces.get(radar);
	}

	private Flux<AircraftTrace> createAircraftTrace(AirportRadar radar) {
		 return Flux.interval(updateInterval)
				 .flatMap(i -> {
					 Instant now = generateCurrentTimestamp();
					 return Flux.fromIterable(radar.updateTraces(now));
				 })
				 .share();
	}

	private Instant generateCurrentTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		return now.withSecond((now.getSecond()))
				.withNano(0)
				.toInstant(ZoneOffset.UTC);
	}

}
