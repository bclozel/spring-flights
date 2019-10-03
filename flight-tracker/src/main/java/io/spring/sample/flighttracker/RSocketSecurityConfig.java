package io.spring.sample.flighttracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.rsocket.PayloadInterceptorOrder;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

/**
 * @author Rob Winch
 */
@Configuration
public class RSocketSecurityConfig {

	@Bean
	PayloadSocketAcceptorInterceptor rsocketSecurity(RSocketSecurity rsocket, AuthenticationPayloadInterceptor jwt) {
		rsocket
			.authorizePayload(authz ->
				authz
					.anyRequest().authenticated()
					.anyExchange().permitAll()
			)
			.addPayloadInterceptor(jwt);
		return rsocket.build();
	}

	@Bean
	AuthenticationPayloadInterceptor jwt(ReactiveJwtDecoder decoder, MetadataExtractorBearerTokenConverter bearerTokenConverter) {
		JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(decoder);
		AuthenticationPayloadInterceptor result = new AuthenticationPayloadInterceptor(manager);
		result.setAuthenticationConverter(bearerTokenConverter);
		result.setOrder(PayloadInterceptorOrder.JWT_AUTHENTICATION.getOrder());
		return result;
	}
}
