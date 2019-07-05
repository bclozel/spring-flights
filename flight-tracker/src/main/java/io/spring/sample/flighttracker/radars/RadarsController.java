package io.spring.sample.flighttracker.radars;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.rsocket.RSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RadarsController {

	private final Logger logger = LoggerFactory.getLogger(RadarsController.class);

	private final RadarService radarService;

	private final Queue<RSocketRequester> connectedClients = new ConcurrentLinkedQueue<>();

	public RadarsController(RadarService radarService) {
		this.radarService = radarService;
	}

	@MessageMapping("locate.radars.within")
	public Flux<AirportLocation> radars(MapRequest request) {
		return this.radarService.findRadars(request.getViewBox(), request.getMaxRadars());
	}

	@MessageMapping("locate.aircrafts.for")
	public Flux<AircraftSignal> aircraftSignal(List<Radar> radars, RSocketRequester requester) {
		this.connectedClients.offer(requester);
		return this.radarService.streamAircraftSignals(radars).onBackpressureDrop()
				.doOnTerminate(() -> {
					this.logger.info("Server error while streaming data to the client");
					this.connectedClients.remove(requester);
				})
				.doOnCancel(() -> {
					this.logger.info("Connection closed by the client");
					this.connectedClients.remove(requester);
				});
	}

	@PostMapping("/location/{iata}")
	public Mono<ResponseEntity> sendClientsToLocation(@PathVariable String iata) {
		return this.radarService.findRadar(iata)
				.flatMapMany(radar -> Flux.fromIterable(this.connectedClients)
						.concatMap(requester -> sendRadarLocation(requester, radar)))
				.then(Mono.just(ResponseEntity.ok("Clients sent to "+ iata)));
	}

	public Mono<Void> sendRadarLocation(RSocketRequester requester, AirportLocation radar) {
		return requester.route("send.to.location")
				.data(radar).send();
	}
}
