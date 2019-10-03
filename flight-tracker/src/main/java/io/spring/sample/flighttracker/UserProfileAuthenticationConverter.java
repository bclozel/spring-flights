package io.spring.sample.flighttracker;

import io.spring.sample.flighttracker.profile.UserProfile;
import io.spring.sample.flighttracker.profile.UserProfileRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Convert the JWT to UserProfileAuthentication which exposes our {@link UserProfile} as
 * the current {@link java.security.Principal}.
 *
 * @author Rob Winch
 */
@Component
public class UserProfileAuthenticationConverter implements
		Converter<Jwt, Mono<AbstractAuthenticationToken>> {
	private JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

	private final UserProfileRepository repository;

	public UserProfileAuthenticationConverter(UserProfileRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<AbstractAuthenticationToken> convert(Jwt source) {
		JwtAuthenticationToken token = (JwtAuthenticationToken) this.converter.convert(source);
		String login = source.getClaim("preferred_username");
		return this.repository.findByLogin(login)
				.switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("Couldn't find the user " + login)))
				.map(profile -> new UserProfileAuthentication(token, profile));
	}

	static class UserProfileAuthentication extends JwtAuthenticationToken {
		private final UserProfile userProfile;

		private UserProfileAuthentication(JwtAuthenticationToken token, UserProfile userProfile) {
			super(token.getToken(), token.getAuthorities());
			this.userProfile = userProfile;
		}

		@Override
		public Object getPrincipal() {
			return this.userProfile;
		}
	}
}
