package io.spring.sample.flighttracker.radars;

public class AirportLocation {

	private String code;

	private String name;

	private LatLng location;

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
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", location=" + location +
				'}';
	}
}
