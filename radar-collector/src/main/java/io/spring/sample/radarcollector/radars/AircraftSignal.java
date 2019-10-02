package io.spring.sample.radarcollector.radars;

import java.time.Instant;

/**
 * A single signal from an existing {@link AircraftTrace}.
 * Once out of range, the signal is marked as lost for the {@link AirportRadar}.
 */
public class AircraftSignal {

	private String callSign;

	private LatLng location;

	private double bearing;

	private Instant captureTime;

	private boolean signalLost;

	AircraftSignal() {
	}

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

	@Override
	public String toString() {
		return "AircraftSignal{" +
				"callSign='" + callSign + '\'' +
				", location=" + location +
				", bearing=" + bearing +
				", captureTime=" + captureTime +
				", signalLost=" + signalLost +
				'}';
	}
}
