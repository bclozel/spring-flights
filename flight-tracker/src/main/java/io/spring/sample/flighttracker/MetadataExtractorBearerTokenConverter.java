package io.spring.sample.flighttracker;

import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.authentication.PayloadExchangeAuthenticationConverter;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Rob Winch
 */
@Component
class MetadataExtractorBearerTokenConverter
		implements PayloadExchangeAuthenticationConverter {

	private final MetadataExtractor metadataExtractor;

	MetadataExtractorBearerTokenConverter(RSocketMessageHandler handler) {
		this.metadataExtractor = handler.getMetadataExtractor();
	}

	@Override
	public Mono<Authentication> convert(PayloadExchange exchange) {
		Map<String, Object> data = this.metadataExtractor
				.extract(exchange.getPayload(), exchange.getMetadataMimeType());
		return Mono.justOrEmpty(data.get(BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE.toString()))
				.cast(String.class).map(BearerTokenAuthenticationToken::new);
	}
}
