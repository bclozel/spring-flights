package io.spring.sample.radarcollector.radars;

import io.spring.sample.radarcollector.airports.Airport;

public class AirportLocation {

	private String name;

	private String iata;

	private LatLng location;

	public AirportLocation() {
	}

	public AirportLocation(Airport airport) {
		this.name = airport.getName();
		this.iata = airport.getIata();
		this.location = new LatLng(airport.getLocation().getY(), airport.getLocation().getX());
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIata() {
		return this.iata;
	}

	public void setIata(String iata) {
		this.iata = iata;
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
				"name='" + name + '\'' +
				", iata='" + iata + '\'' +
				", location=" + location +
				'}';
	}
}
