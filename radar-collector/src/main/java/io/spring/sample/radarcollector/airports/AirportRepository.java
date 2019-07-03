package io.spring.sample.radarcollector.airports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AirportRepository extends ReactiveMongoRepository<Airport, String> {

	Mono<Airport> findByIata(String iata);

	Flux<Airport> findByLocationNear(Point point, Pageable pageable);

	Flux<Airport> findByLocationWithin(Box box, Pageable pageable);

}
