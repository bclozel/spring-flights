package io.spring.sample.radarcollector.radars;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.spring.sample.radarcollector.airports.Airport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportRadar {

	private static final Logger logger = LoggerFactory.getLogger(AirportRadar.class);

	private static final Random RANDOM = new Random();

	private String name;

	private String iata;

	private LatLng location;

	private double range;

	private int aircraftCount;

	private final List<AircraftTrace> traces;

	public AirportRadar(Airport airport) {
		this(airport, 100, 10);
	}

	public AirportRadar(Airport airport, double range, int aircraftCount) {
		this.name = airport.getName();
		this.iata = airport.getIata();
		this.location = new LatLng(airport.getLocation().getY(), airport.getLocation().getX());
		this.range = range;
		this.aircraftCount = aircraftCount;
		this.traces = createRandomAircraftTraces(aircraftCount);
	}

	private List<AircraftTrace> createRandomAircraftTraces(int aircraftCount) {
		List<AircraftTrace> list = new ArrayList<>();
		for (int i = 0; i < aircraftCount; i++) {
			list.add(AircraftTrace.createWithinDistanceFromReferencePoint(this.location, this.range));
		}
		return list;
	}

	public String getName() {
		return this.name;
	}

	public String getIata() {
		return this.iata;
	}

	public LatLng getLocation() {
		return this.location;
	}

	public double getRange() {
		return this.range;
	}

	public List<AircraftTrace> updateTraces(Instant currentTime) {
		this.traces.removeIf(AircraftTrace::isSignalLost);
		this.traces.forEach(trace -> {
			trace.updateLocation(currentTime);
			double dist = trace.distanceFromPoint(this.location);
			if (dist > this.range) {
				trace.setSignalLost(true);
			}
		});
		while (this.traces.size() < this.aircraftCount) {
			this.traces.add(spawnNewAircraft());
		}
		return this.traces;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AirportRadar that = (AirportRadar) o;
		return iata.equals(that.iata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(iata);
	}

	/**
	 * Spawn a new aircraft in a plausible place, i.e. taking off from the airport
	 * or at the edge of the radar's range.
	 */
	private AircraftTrace spawnNewAircraft() {
		boolean fromAirport = RANDOM.nextBoolean();
		if (fromAirport) {
			return AircraftTrace.createAtLocation(this.getLocation());
		}
		else {
			return AircraftTrace.createAtDistanceFromReferencePoint(this.location, this.range);
		}
	}

}
