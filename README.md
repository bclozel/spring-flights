## Spring Flights Application

This is a demo application showcasing RSocket support in Spring.

This application is made of 3 modules:

* `radar-collector`, an app that provides information about airport radars and the aircraft signals they collect.
* `flight-tracker` and `flight-client`, an app that displays an interactive map with radars and aircrafts.

As a first contact with the RSocket protocol, check out the `demo-backpressure` application.


### Running the applications

First, run the collector application:

```
$ ./gradlew :radar-collector:build
$ java -jar radar-collector/build/libs/radar-collector-0.0.1-SNAPSHOT.jar
```

Then, run the tracker web application:
```
$ ./gradlew :flight-tracker:build
$ java -jar flight-tracker/build/libs/flight-tracker-0.0.1-SNAPSHOT.jar
```

The tracker application is available at `http://localhost:8080/`

### Radar Collector

This application stores the name, code and location of airports around the world in a MongoDB database.
For each airport, it is able to generate random aircraft signals data received by the local airport radar.

Clients can then request for a list of airport radars within a particular area and get a stream of aircraft
signals for airport radars.

The application starts an RSocket server with TCP transport, at `localhost:9898`.

Currently you cannot use the [rsocket-cli](https://github.com/rsocket/rsocket-cli) because it
does not yet support composite metadata. However you can [use the following tests](radar-collector/src/test/java/io/spring/sample/radarcollector/RadarCollectorApplicationTests.java).

### Flight Tracker

This application displays an interactive map showing radars and aircraft flying around them.
It stores user profile and preferences, like favorite airport, in a local database.

The application starts a WebFlux server at `localhost:8080`, with an RSocket over websocket endpoint on `/rsocket`.
The `flight-client` module builds the JavaScript client using [Leaflet](https://leafletjs.com/) and the the websocket client
from [rsocket-js](https://github.com/rsocket/rsocket-js/).

The browser will first locate all radars in the current view box; you can do the same on the CLI with:

```
rsocket-cli --stream \
--metadataFormat=application/vnd.spring.rsocket.metadata+json -m='{"route":"locate.radars.within"}' \
--dataFormat=json -i='{"viewBox": {"first":{"lng": 3.878915, "lat": 46.409025}, "second": {"lng": 6.714843, "lat": 44.365644}}, "maxRadars": 10}' \
--debug ws://localhost:8080/rsocket
```

Once all the radars are retrieved, we can ask a merged stream of all aircrafts for those radars to the server.

```
rsocket-cli --stream \
--metadataFormat=application/vnd.spring.rsocket.metadata+json -m='{"route":"locate.aircrafts.for"}' \
--dataFormat=json -i='[{"code":"LYS"}, {"code":"CVF"}, {"code":"NCY"}]' \
--debug ws://localhost:8080/rsocket
```

The browser will perform similar requests and update the aircrafts' positions live.

The Leaflet map has a number input (bottom left) which controls the reactive streams demand from the client.
Decreasing it significantly should make the server send less updates to the map. Increasing it back should
catch up with the updates.

The map also has a text input (bottom right) where users can enter another user's login to go to their
favorite airport and see them on the map.

Also, once the RSocket client is connected to the server, a bi-directionnal connection is established:
they're now both able to send requests (being a requester) and respond to those (being a responder).
Here, this demo shows how the JavaScript client can respond to requests sent by the server.

Sending the following request to the web server will make it send requests to all connected clients
to let them know that they should change their location to the selected radar:

```
curl -X POST localhost:8080/location/CDG
```