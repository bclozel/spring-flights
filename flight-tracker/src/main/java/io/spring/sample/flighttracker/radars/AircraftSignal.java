package io.spring.sample.flighttracker.radars;

import java.time.Instant;

public class AircraftSignal {

	private String callSign;

	private LatLng location;

	private double bearing;

	private Instant captureTime;

	private boolean signalLost;

	public String getCallSign() {
		return this.callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public LatLng getLocation() {
		return this.location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public double getBearing() {
		return this.bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public Instant getCaptureTime() {
		return this.captureTime;
	}

	public void setCaptureTime(Instant captureTime) {
		this.captureTime = captureTime;
	}

	public boolean isSignalLost() {
		return this.signalLost;
	}

	public void setSignalLost(boolean signalLost) {
		this.signalLost = signalLost;
	}
}
