package io.spring.sample.radarcollector.radars;

import java.time.Duration;
import java.time.Instant;

import io.spring.sample.radarcollector.airports.Airport;
import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * @author Brian Clozel
 */
public class Something {

	public static void main(String[] args) {
		AirportRadar radar = new AirportRadar(new Airport("LYS", "LYS", new GeoJsonPoint(4.832147, 45.757237)));
		double range = 200;
		AircraftTrace trace = AircraftTrace.createAtDistanceFromReferencePoint(radar.getLocation(), range);
		double destinationBearing = AircraftTrace.calculateBearing(trace.getLocation(), radar.getLocation());
		double traceBearing = trace.getBearing();

		System.out.println("trace location: " + trace.getLocation() + " bearing: " + traceBearing + ", destination bearing: " + destinationBearing);
		Flux.interval(Duration.ofSeconds(1))
				.doOnNext(i -> {
					trace.updateLocation(Instant.now());
					System.out.println("new location: " + trace.getLocation());
					System.out.println("distance from reference: " + trace.distanceFromPoint(radar.getLocation()));
				})
				.take(50).blockLast();
	}
}
