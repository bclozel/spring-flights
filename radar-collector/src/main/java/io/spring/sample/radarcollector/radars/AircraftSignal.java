package io.spring.sample.radarcollector.radars;

import java.time.Instant;

public class AircraftSignal {

	private final String callSign;

	private final LatLng location;

	private final double bearing;

	private final Instant captureTime;

	private boolean signalLost;


	public AircraftSignal(AircraftTrace trace) {
		this.callSign = trace.getCallSign();
		this.location = trace.getLocation();
		this.bearing = trace.getBearing();
		this.captureTime = trace.getCurrentTime();
		this.signalLost = trace.isSignalLost();
	}

	public String getCallSign() {
		return this.callSign;
	}

	public LatLng getLocation() {
		return this.location;
	}

	public double getBearing() {
		return this.bearing;
	}

	public Instant getCaptureTime() {
		return this.captureTime;
	}

	public boolean isSignalLost() {
		return this.signalLost;
	}

	public void setSignalLost(boolean signalLost) {
		this.signalLost = signalLost;
	}
}
