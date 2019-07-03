package io.spring.sample.radarcollector.radars;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;


public class ViewBox {

	private LatLng first;

	private LatLng second;

	public ViewBox(LatLng first, LatLng second) {
		this.first = first;
		this.second = second;
	}

	public LatLng getFirst() {
		return this.first;
	}

	public LatLng getSecond() {
		return this.second;
	}

	public Box toGeoBox() {
		return new Box(new Point(this.first.getLng(), this.first.getLat()),
				new Point(this.second.getLng(), this.second.getLat()));
	}
}
