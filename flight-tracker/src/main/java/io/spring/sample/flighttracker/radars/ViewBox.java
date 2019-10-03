package io.spring.sample.flighttracker.radars;


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
		return first;
	}

	public void setFirst(LatLng first) {
		this.first = first;
	}

	public LatLng getSecond() {
		return second;
	}

	public void setSecond(LatLng second) {
		this.second = second;
	}
}
