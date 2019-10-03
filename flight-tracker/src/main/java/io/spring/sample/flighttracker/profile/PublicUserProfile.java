package io.spring.sample.flighttracker.profile;

public class PublicUserProfile {

	private String login;

	private String name;

	private String avatarUrl;

	private FavoriteAirport airport;

	public PublicUserProfile() {
	}

	public PublicUserProfile(UserProfile profile) {
		this.login = profile.getLogin();
		this.name = profile.getName();
		this.avatarUrl = profile.getAvatarUrl();
		this.airport = profile.getAirport();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public FavoriteAirport getAirport() {
		return airport;
	}

	public void setAirport(FavoriteAirport airport) {
		this.airport = airport;
	}
}
