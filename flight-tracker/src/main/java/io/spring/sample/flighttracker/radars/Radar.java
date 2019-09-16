package io.spring.sample.flighttracker.radars;

public class Radar {

	private String type;

	private String code;

	public Radar() {
	}

	public Radar(String type, String code) {
		this.type = type;
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
