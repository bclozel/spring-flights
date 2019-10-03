package io.spring.sample.flighttracker.profile;

public class FavoriteAirport {

	private String code;

	private String name;

	private double lat;

	private double lng;

	public FavoriteAirport() {
	}

	public FavoriteAirport(String code, String name, double lat, double lng) {
		this.code = code;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
