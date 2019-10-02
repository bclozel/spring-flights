package io.spring.sample.flighttracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

/**
 * @author Rob Winch
 */
@Configuration
public class RSocketSecurityConfig {

	@Bean
	PayloadSocketAcceptorInterceptor rsocketSecurity(RSocketSecurity rsocket) {
		rsocket
			.authorizePayload(authz ->
				authz
					.anyRequest().authenticated()
					.anyExchange().permitAll()
			)
			.jwt(Customizer.withDefaults());
		return rsocket.build();
	}
}
