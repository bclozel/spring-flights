package io.spring.sample.flighttracker.radars;


public class AirportLocation {

	private String name;

	private String iata;

	private LatLng location;

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
		return this.location;
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
