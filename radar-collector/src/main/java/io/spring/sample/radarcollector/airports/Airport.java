package io.spring.sample.radarcollector.airports;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Airport {

	@Id
	private String id;

	private String name;

	private String iata;

	private GeoJsonPoint location;

	public Airport() {
	}

	public Airport(String name, String iata, GeoJsonPoint location) {
		this.name = name;
		this.iata = iata;
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIata() {
		return iata;
	}

	public void setIata(String iata) {
		this.iata = iata;
	}

	public GeoJsonPoint getLocation() {
		return location;
	}

	public void setLocation(GeoJsonPoint location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Airport{" +
				"name='" + name + '\'' +
				", iata='" + iata + '\'' +
				'}';
	}
}
