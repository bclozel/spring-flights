package io.spring.sample.radarcollector.airports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class AirportRepositoryInitializer {

	private final Logger logger = LoggerFactory.getLogger(AirportRepositoryInitializer.class);

	private final MongoTemplate template;

	private final MongoMappingContext mappingContext;

	private final ObjectMapper objectMapper;

	public AirportRepositoryInitializer(MongoTemplate template, MongoMappingContext mappingContext, ObjectMapper objectMapper) {
		this.template = template;
		this.mappingContext = mappingContext;
		this.objectMapper = objectMapper;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeAirportsDatabase() throws IOException {
		IndexOperations indexOps = template.indexOps(Airport.class);
		IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
		resolver.resolveIndexFor(Airport.class).forEach(indexOps::ensureIndex);
		if (this.template.count(query(Criteria.where("code").exists(true)), Airport.class) == 0) {
			ClassPathResource airportsResource = new ClassPathResource("airports.json");
			AirportsFileEntry[] fileEntries = this.objectMapper
					.readValue(airportsResource.getInputStream(), AirportsFileEntry[].class);

			List<Airport> airports = Arrays.stream(fileEntries)
					.map(entry -> new Airport(entry.getCode(), entry.getName(),
							new GeoJsonPoint(entry.getLon(), entry.getLat())))
					.collect(Collectors.toList());

			Collection<Airport> inserted = this.template.insert(airports, Airport.class);
			logger.info("Added {} airports to the database", inserted.size());
		}
	}
}
