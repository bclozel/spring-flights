import {RSocketClient, JsonSerializer} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {Metadata, JsonMetadataSerializer} from './metadata'

export class TrackerClient {

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
        return new Promise(((resolve, reject) => {
            this.client.connect().subscribe({
                onComplete: s => {
                    this.socket = s;
                    resolve(this.socket);
                },
                onError: error => reject(error),
                onSubscribe: cancel => { this.cancel = cancel}
            });
        }));
    }

    locateRadars(x, y, max) {
        let radars = [];
        return new Promise(((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, 'locate.radars.within');
            return this.socket.requestStream({
                data: {viewBox: {first: x, second: y}, maxRadars: max},
                metadata: metadata,
            }).subscribe({
                onSubscribe: sub => sub.request(max),
                onError: error => reject(error),
                onNext: msg => radars.push(msg.data),
                onComplete: () => resolve(radars)
            });
        }));
    }

    streamAircraftPositions(airports) {
        let metadata = new Metadata();
        metadata.set(Metadata.ROUTE, 'locate.aircrafts.for');
        return this.socket.requestStream({
            data: airports,
            metadata: metadata,
        });
    }

    locateRadar(code) {
        return new Promise((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, `locate.radar.${code}`);
            this.socket.requestResponse({
                metadata: metadata,
            }).subscribe({
                onComplete: msg => resolve(msg.data),
                onError: error => reject(error)
            });
        });
    }

    fetchUserProfile(login) {
        return new Promise((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, `fetch.profile.${login}`);
            this.socket.requestResponse({
                metadata: metadata,
            }).subscribe({
                onComplete: msg => resolve(msg.data),
                onError: error => reject(error)
            });
        });
    }

    disconnect() {
        this.cancel();
    }

}