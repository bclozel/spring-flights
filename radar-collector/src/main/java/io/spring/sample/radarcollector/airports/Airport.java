package io.spring.sample.radarcollector.airports;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Airport {

	@Id
	private String id;

	private String code;

	private String name;

	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private GeoJsonPoint location;

	public Airport() {
	}

	public Airport(String code, String name, GeoJsonPoint location) {
		this.code = code;
		this.name = name;
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public GeoJsonPoint getLocation() {
		return location;
	}

	public void setLocation(GeoJsonPoint location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Airport{" +
				"id='" + id + '\'' +
				", code='" + code + '\'' +
				'}';
	}
}
