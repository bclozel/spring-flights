## Spring Flights Application

This is a demo application showcasing RSocket support in Spring.

This application is made of 3 modules:

* `radar-collector`, an app that provides information about radars and the aircraft signals they collect.
* `flight-tracker` and `flight-client`, an app that displays an interactive map with radars and aircrafts.

### Radar Collector

This application is providing information about radars (here, airports): their IATA code, location
 and aircraft signals recorded. The aircraft signals are randomly generated and the actual list
of radars is actually created from a list of airports, inserted in a MongoDB database.

The application starts an RSocket server with TCP transport, at `localhost:9898`.

You can get a list of airports located inside specific coordinates,
using the [rsocket-cli](https://github.com/rsocket/rsocket-cli):

```
rsocket-cli --stream --metadataFormat=message/x.rsocket.routing.v0 -m=locate.radars.within --dataFormat=json \
-i='{"first":{"lng": 3.878915, "lat": 46.409025}, "second": {"lng": 6.714843, "lat": 44.365644}}' tcp://localhost:9898/
```

You can also get a stream of aircraft locations for a given radar, with:

```
rsocket-cli --stream --metadataFormat=message/x.rsocket.routing.v0 -m=listen.radar.LYS --dataFormat=json -i "" tcp://localhost:9898/
```


### Flight Tracker

This application displays an interactive map showing radars - it is also concatenating
the streams of aircraft signals for the radars displayed on screen.

The application starts a WebFlux server at `localhost:8080`, with an RSocket over websocket endpoint on `/rsocket`.
The `flight-client` module builds the JavaScript client using [Leaflet](https://leafletjs.com/) and the the websocket client
from [rsocket-js](https://github.com/rsocket/rsocket-js/).

The browser will first locate all radars in the current view box; you can do the same on the CLI with:

```
rsocket-cli --stream --metadataFormat=message/x.rsocket.routing.v0 -m=locate.radars.within --dataFormat=json \
-i='{"viewBox": {"first":{"lng": 3.878915, "lat": 46.409025}, "second": {"lng": 6.714843, "lat": 44.365644}}, "maxRadars": 10}' ws://localhost:8080/rsocket
```

Once all the radars are retrieved, we can ask a merged stream of all aircrafts for those radars to the server.

```
rsocket-cli --stream --metadataFormat=message/x.rsocket.routing.v0 -m=locate.aircrafts.for --dataFormat=json \
-i='[{"iata":"LYS"}, {"iata":"CVF"}, {"iata":"NCY"}]' ws://localhost:8080/rsocket
```

The browser will perform such a request and update the aircrafts positions live.

The Leaflet map has a small number input (bottom left) which controls the reactive streams demand from the client.
Decreasing it significantly should make the server send less updates to the map. Increasing it back should
catch up with the updates.

Also, once the RSocket client is connected to the server, a bi-directionnal connection is established:
they're now both able to send requests (being a requester) and respond to those (being a responder).
Here, this demo shows how the JavaScript client can respond to requests sent by the server.

Sending the following request to the web server will make it send requests to all connected clients
to let them know that they should change their location to the selected radar:

```
curl -X POST localhost:8080/location/CDG
```