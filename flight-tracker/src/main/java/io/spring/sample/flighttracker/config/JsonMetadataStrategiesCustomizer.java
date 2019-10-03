package io.spring.sample.flighttracker.config;

import java.util.Map;

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

/**
 * {@link RSocketStrategiesCustomizer} that adds a custom extractor to the
 * {@link org.springframework.messaging.rsocket.MetadataExtractorRegistry}.
 * We're using here an alternate metadata format that uses a JSON object
 * to hold multiple metadata entries with a custom JSON media type.
 * <p>This configuration won't be necessary when the rsocket-js
 * library will support the RSocket composite metadata extension.
 */
@Component
public class JsonMetadataStrategiesCustomizer implements RSocketStrategiesCustomizer {

	public static final MimeType METADATA_MIME_TYPE = MimeType.valueOf("application/vnd.spring.rsocket.metadata+json");

	private static final ParameterizedTypeReference<Map<String, String>> METADATA_TYPE =
			new ParameterizedTypeReference<Map<String, String>>() {};

	@Override
	public void customize(RSocketStrategies.Builder strategies) {
		strategies.metadataExtractorRegistry(registry -> {
			registry.metadataToExtract(METADATA_MIME_TYPE, METADATA_TYPE, (in, map) -> {
				map.putAll(in);
			});
		});
	}
	
}
