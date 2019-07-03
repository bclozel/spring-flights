package io.spring.sample.radarcollector.radars;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LatLng {

	private double lat;

	private double lng;

	public LatLng() {
	}

	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return this.lat;
	}

	@JsonIgnore
	public double getLatRad() {
		return Math.toRadians(this.lat);
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return this.lng;
	}

	@JsonIgnore
	public double getLngRad() {
		return Math.toRadians(this.lng);
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LatLng{" +
				"lat=" + lat +
				", lng=" + lng +
				'}';
	}
}
