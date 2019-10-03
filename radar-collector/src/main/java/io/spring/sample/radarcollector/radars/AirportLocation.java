package io.spring.sample.radarcollector.radars;

import io.spring.sample.radarcollector.airports.Airport;

/**
 * Location of an airport.
 */
public class AirportLocation {

	private String code;
	
	private String name;

	private LatLng location;

	public AirportLocation() {
	}

	public AirportLocation(Airport airport) {
		this.code = airport.getCode();
		this.name = airport.getName();
		this.location = new LatLng(airport.getLocation().getY(), airport.getLocation().getX());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "AirportLocation{" +
				"code='" + code + '\'' +
				", name='" + name + '\'' +
				", location=" + location +
				'}';
	}
}
