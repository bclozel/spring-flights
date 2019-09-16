import {RSocketClient, JsonSerializer} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {Metadata, JsonMetadataSerializer} from './metadata'

export class RadarClient {

    constructor(url, responder) {
        this.client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: JsonMetadataSerializer,
            },
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 10000,
                // ms timeout if no keepalive response
                lifetime: 20000,
                dataMimeType: 'application/json',
                metadataMimeType: JsonMetadataSerializer.MIME_TYPE,
            },
            transport: new RSocketWebSocketClient({url: url}),
            responder: responder
        });
    }

    connect(cb) {
        return this.client.connect().subscribe({
            onComplete: s => {
                this.socket = s;
                cb();
            },
            onError: error => console.error(error),
            onSubscribe: cancel => { this.cancel = cancel}
        });
    }

    locateRadars(x, y, max) {
        let metadata = new Metadata();
        metadata.set(Metadata.ROUTE, 'locate.radars.within');
        return this.socket.requestStream({
            data: {viewBox: {first: x, second: y}, maxRadars: max},
            metadata: metadata,
        });
    }

    streamAircraftPositions(airports) {
        let metadata = new Metadata();
        metadata.set(Metadata.ROUTE, 'locate.aircrafts.for');
        return this.socket.requestStream({
            data: airports,
            metadata: metadata,
        });
    }

    disconnect() {
        this.cancel();
    }

}