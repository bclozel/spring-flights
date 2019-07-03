package io.spring.sample.radarcollector.radars;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * <a href="https://www.movable-type.co.uk/scripts/latlong.html">LatLng resources</a>.
 */
public class AircraftTrace {

	private static final double EARTH_RADIUS = 6371;

	private static final String IATA_LETTERS = "ABCDEFGHIFKLMNOPQRSTUVWXYZ";

	private static final String FLIGHT_NUMBERS = "0123456789";

	private static final Random RANDOM = new Random();

	private String callSign;

	private LatLng location;

	private double speed;

	private double bearing;

	private Instant currentTime;

	private boolean signalLost;

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getBearing() {
		return this.bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public Instant getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Instant currentTime) {
		this.currentTime = currentTime;
	}

	public boolean isSignalLost() {
		return signalLost;
	}

	public void setSignalLost(boolean signalLost) {
		this.signalLost = signalLost;
	}

	public void updateLocation(Instant currentTime) {
		long timeDiff = currentTime.getEpochSecond() - this.currentTime.getEpochSecond();
		double distance = this.speed / 3600 * timeDiff;
		this.currentTime = currentTime;
		LatLng newCoordinates = calculateDestination(this.location, this.bearing, distance);
		this.setLocation(newCoordinates);
	}

	public double distanceFromPoint(LatLng pointLocation) {
		// See https://rosettacode.org/wiki/Haversine_formula#Java
		double dLat = Math.toRadians(pointLocation.getLat() - this.location.getLat());
		double dLon = Math.toRadians(pointLocation.getLng() - this.location.getLng());
		double traceLat = this.location.getLatRad();
		double pointLat = pointLocation.getLatRad();
		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(pointLat) * Math.cos(traceLat);
		double c = 2 * Math.asin(Math.sqrt(a));
		return EARTH_RADIUS * c;
	}

	@Override
	public String toString() {
		return "AircraftTrace{" +
				"callSign='" + callSign + '\'' +
				", location=" + location +
				", speed=" + speed +
				", bearing=" + bearing +
				", currentTime=" + currentTime +
				'}';
	}

	public static AircraftTrace createAtLocation(LatLng location) {
		return createAtLocation(location, RANDOM.nextInt(360));
	}

	static AircraftTrace createAtLocation(LatLng location, double bearing) {
		AircraftTrace trace = new AircraftTrace();
		StringBuilder builder = new StringBuilder();
		builder.append(IATA_LETTERS.charAt(RANDOM.nextInt(IATA_LETTERS.length())));
		builder.append(IATA_LETTERS.charAt(RANDOM.nextInt(IATA_LETTERS.length())));
		builder.append(FLIGHT_NUMBERS.charAt(RANDOM.nextInt(FLIGHT_NUMBERS.length())));
		builder.append(FLIGHT_NUMBERS.charAt(RANDOM.nextInt(FLIGHT_NUMBERS.length())));
		builder.append(FLIGHT_NUMBERS.charAt(RANDOM.nextInt(FLIGHT_NUMBERS.length())));
		trace.setCallSign(builder.toString());
		trace.setBearing(bearing);
		trace.setSpeed(RANDOM.nextInt(200) + 300);
		trace.setLocation(location);
		trace.setCurrentTime(generateCurrentTimestamp());
		return trace;
	}

	private static Instant generateCurrentTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		return now.withSecond((now.getSecond()))
				.withNano(0)
				.toInstant(ZoneOffset.UTC);
	}

	static AircraftTrace createAtDistanceFromReferencePoint(LatLng referenceLocation, double distance) {
		double randomAngle = RANDOM.nextDouble() * 360;
		LatLng location = calculateDestination(referenceLocation, randomAngle, distance);
		double bearingToReference = calculateBearing(location, referenceLocation);
		double bearing = bearingToReference - 30 + RANDOM.nextInt(60);
		return createAtLocation(location, bearing);
	}

	static AircraftTrace createWithinDistanceFromReferencePoint(LatLng referenceLocation, double distance) {
		return createAtDistanceFromReferencePoint(referenceLocation, RANDOM.nextDouble() * distance);
	}

	private static LatLng calculateDestination(LatLng location, double bearing, double distance) {
		double startLat = location.getLatRad();
		double startLng = location.getLngRad();
		double angularDistance = distance / EARTH_RADIUS;
		double bearingRad = Math.toRadians(bearing);
		double endLat = Math.asin(Math.sin(startLat) * Math.cos(angularDistance) +
				Math.cos(startLat) * Math.sin(angularDistance) * Math.cos(bearingRad));
		double endLng = startLng + Math.atan2(Math.sin(bearingRad) * Math.sin(angularDistance) * Math.cos(startLat),
				Math.cos(angularDistance) - Math.sin(startLat) * Math.sin(endLat));
		return new LatLng(Math.toDegrees(endLat),
				(Math.toDegrees(endLng) + 540) % 360 - 180);
	}

	public static double calculateBearing(LatLng start, LatLng end) {
		/*
		var y = Math.sin(λ2-λ1) * Math.cos(φ2);
var x = Math.cos(φ1)*Math.sin(φ2) -
        Math.sin(φ1)*Math.cos(φ2)*Math.cos(λ2-λ1);
var brng = Math.atan2(y, x).toDegrees();
		 */

		double startLat = start.getLatRad();
		double startLng = start.getLngRad();
		double endLat = end.getLatRad();
		double endLng = end.getLngRad();
		double y = Math.sin(endLng - startLng) * Math.cos(endLat);
		double x = Math.cos(startLat) * Math.sin(endLat)
				- Math.sin(startLat) * Math.cos(endLat) * Math.cos(endLng - startLng);
		return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
	}

}
