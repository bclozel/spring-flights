package io.spring.sample.flighttracker;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Rob Winch
 */
@Component
public class Friends {
	private Map<String, Set<String>> loginToFriendLogins = new HashMap<>();

	public Friends() {
		// Brian and Rossen are friends
		addFriendship("brian", "rossen");
	}

	private void addFriendship(String loginA, String loginB) {
		this.loginToFriendLogins.computeIfAbsent(loginA, k -> new HashSet<>()).add(loginB);
		this.loginToFriendLogins.computeIfAbsent(loginB, k -> new HashSet<>()).add(loginA);
	}

	public boolean exist(String currentUserLogin, String loginToTest) {
		return this.loginToFriendLogins.getOrDefault(currentUserLogin, Collections.emptySet())
				.contains(loginToTest);
	}
}
