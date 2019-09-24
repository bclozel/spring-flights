package io.spring.sample.flighttracker.profile;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserProfile {

	@Id
	private String id;

	@Indexed(unique = true)
	private String login;

	private String name;

	private String email;

	private String avatarUrl;

	private String favoriteAirport;

	public UserProfile() {
	}

	public UserProfile(String login, String name, String email, String avatarUrl, String favoriteAirport) {
		this.login = login;
		this.name = name;
		this.email = email;
		this.avatarUrl = avatarUrl;
		this.favoriteAirport = favoriteAirport;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getFavoriteAirport() {
		return favoriteAirport;
	}

	public void setFavoriteAirport(String favoriteAirport) {
		this.favoriteAirport = favoriteAirport;
	}
}
