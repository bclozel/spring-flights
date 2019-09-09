import {
    RSocketClient,
    JsonSerializer,
    IdentitySerializer,
    BufferEncoder,
    UTF8Encoder
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {RoutingMetadataSerializer} from './metadata'

const CustomEncoders = {
    data: UTF8Encoder,
    dataMimeType: UTF8Encoder,
    message: UTF8Encoder,
    metadata: BufferEncoder,
    metadataMimeType: UTF8Encoder,
    resumeToken: UTF8Encoder,
};

export class RadarClient {

    constructor(url, responder) {
        this.client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: new RoutingMetadataSerializer(),
            },
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 10000,
                // ms timeout if no keepalive response
                lifetime: 20000,
                dataMimeType: 'application/json',
                metadataMimeType: RoutingMetadataSerializer.MIME_TYPE,
            },
            transport: new RSocketWebSocketClient({url: url}, CustomEncoders),
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
        return this.socket.requestStream({
            data: {viewBox: {first: x, second: y}, maxRadars: max},
            metadata: 'locate.radars.within',
        });
    }

    streamAircraftPositions(airports) {
        return this.socket.requestStream({
            data: airports,
            metadata: 'locate.aircrafts.for',
        });
    }

    disconnect() {
        this.cancel();
    }

}