package io.spring.sample.flighttracker.radars;

public class MapRequest {

	private ViewBox viewBox;

	private int maxRadars;

	public MapRequest() {
	}

	public MapRequest(ViewBox viewBox, int maxRadars) {
		this.viewBox = viewBox;
		this.maxRadars = maxRadars;
	}

	public ViewBox getViewBox() {
		return viewBox;
	}

	public void setViewBox(ViewBox viewBox) {
		this.viewBox = viewBox;
	}

	public int getMaxRadars() {
		return maxRadars;
	}

	public void setMaxRadars(int maxRadars) {
		this.maxRadars = maxRadars;
	}
}
