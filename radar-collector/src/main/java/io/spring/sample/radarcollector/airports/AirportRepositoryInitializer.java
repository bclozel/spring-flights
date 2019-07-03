package io.spring.sample.radarcollector.airports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class AirportRepositoryInitializer {

	private final Logger logger = LoggerFactory.getLogger(AirportRepositoryInitializer.class);

	private final MongoTemplate template;

	private final ObjectMapper objectMapper;

	public AirportRepositoryInitializer(MongoTemplate template, ObjectMapper objectMapper) {
		this.template = template;
		this.objectMapper = objectMapper;
	}

	@PostConstruct
	public void initializeAirportsDatabase() throws IOException {
		if (!this.template.collectionExists(Airport.class)) {
			this.template.createCollection(Airport.class);
			IndexOperations indexOps = this.template.indexOps(Airport.class);
			indexOps.ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
			indexOps.ensureIndex(new Index("iata", Sort.Direction.ASC).unique());
		}
		if (this.template.count(query(Criteria.where("iata").exists(true)), Airport.class) == 0) {
			ClassPathResource airportsResource = new ClassPathResource("airports.json");
			AirportsFileEntry[] fileEntries = this.objectMapper
					.readValue(airportsResource.getInputStream(), AirportsFileEntry[].class);

			List<Airport> airports = Arrays.stream(fileEntries)
					.filter(AirportsFileEntry::isOpened)
					.map(entry -> new Airport(entry.getName(), entry.getIata(), new GeoJsonPoint(entry.getLon(), entry.getLat())))
					.collect(Collectors.toList());

			Collection<Airport> inserted = this.template.insert(airports, Airport.class);
			logger.info("Added {} airports to the database", inserted.size());

			Point first = new Point(3.878915,  46.409025);
			Point second = new Point(6.714843, 44.365644);
			Box box = new Box(first, second);
			List<Airport> result = this.template.find(query(Criteria.where("location").within(box)), Airport.class);
			logger.info(result.toString() );
			/*


			 */
		}
	}
}
