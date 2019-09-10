package io.spring.sample.radarcollector.radars;

import org.springframework.data.geo.Box;


public class ViewBox {

	private LatLng first;

	private LatLng second;

	public ViewBox() {
	}

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
		return new Box(this.first.toPoint(), this.second.toPoint());
	}

}
