import {RadarClient, PartialResponder} from './radars';
import L from "leaflet";
import 'leaflet/dist/leaflet.css';
import 'leaflet-rotatedmarker/leaflet.rotatedMarker'
import planeImg from './img/airplane.png';
import radarImg from './img/satellite-dish.png';
import {Metadata} from "./metadata";

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const planeIcon = L.icon({
    iconUrl: planeImg,
    iconSize: [64, 64],
    popupAnchor: [-3, -76],
    shadowUrl: '',
});
const radarIcon = L.icon({
    iconUrl: radarImg,
    iconSize: [64, 64],
    popupAnchor: [-3, -76],
    shadowUrl: ''
});

const maxRadars = 6;


export class RadarMap {

    constructor(coordinates, zoomLevel) {
        this.L = L;
        this.map = this.L.map('map').setView(coordinates, zoomLevel);
        var mainLayer = this.L.tileLayer('https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}{r}.png', {
            attribution: '<a href="https://wikimediafoundation.org/wiki/Maps_Terms_of_Use">Wikimedia</a>',
            minZoom: 1,
            maxZoom: 19
        });
        mainLayer.addTo(this.map);

        this.radars = [];
        this.radarsLayer = L.layerGroup();
        this.radarsLayer.addTo(this.map);

        this.signals = new Map();
        this.signalsLayer = L.layerGroup();
        this.signalsLayer.addTo(this.map);

        this.radarClient = new RadarClient('ws://localhost:8080/rsocket', new MapHandler(this.map));
        this.radarClient.connect(() => this.connectCallback());

        this.backpressureCtrl = new L.Control.BackpressureCtrl();
        this.backpressureCtrl.addTo(this.map);
    }

    connectCallback() {
        this.showLiveContent();
        this.map.on('load', this.showLiveContent, this);
        this.map.on('movestart', this.moveStart, this);
        this.map.on('moveend', this.updateMap, this);
        this.map.on('zoomstart', this.moveStart, this);
        this.map.on('zoomend', this.updateMap, this);
    }

    moveStart() {
        this.backpressureCtrl.cancel();
    }

    updateMap() {
        this.radars.forEach(radar => radar.removeFromLayer(this.radarsLayer));
        this.radars = [];
        this.signals.forEach(signal => signal.removeFromLayer(this.signalsLayer));
        this.signals.clear();
        this.showLiveContent();
    }

    showLiveContent() {
        const bounds = this.map.getBounds();
        this.radarClient.locateRadars(bounds.getNorthEast(), bounds.getSouthWest(), maxRadars)
            .subscribe({
                onError: error => console.error(error),
                onNext: msg => {
                    const radar = new Radar(msg.data);
                    radar.addToLayer(this.radarsLayer);
                    this.radars.push(radar);
                },
                onComplete: () => {
                    const radars = this.radars.map(v => {
                        const radar = {code: v.code};
                        return radar;
                    });
                    this.radarClient
                        .streamAircraftPositions(radars)
                        .subscribe({
                            onError: error => {
                                console.error(error);
                                console.dir(error);
                            },
                            onNext: msg => {
                                const data = msg.data;
                                if (this.signals.has(data.callSign)) {
                                    const signal = this.signals.get(data.callSign);
                                    if (data.signalLost) {
                                        this.signals.delete(data.callSign);
                                        signal.removeFromLayer(this.signalsLayer);
                                    } else {
                                        signal.update(data);
                                    }
                                } else {
                                    const signal = new AircraftSignal(data);
                                    signal.addToLayer(this.signalsLayer);
                                    this.signals.set(signal.callSign, signal);
                                }
                            },
                            onSubscribe: sub => {
                                this.backpressureCtrl.useSubscription(sub);
                            },

                        });
                },
                onSubscribe: sub => sub.request(maxRadars),
            });
    }
}

L.Control.BackpressureCtrl = L.Control.extend({
    options: {
        position: 'bottomleft',
        min: 1,
        max: 200,
        defaultValue: 50
    },

    onAdd: function(map) {
        var input = L.DomUtil.create('input', "backpressure-control");
        input.type = 'number';
        input.min = this.options.min;
        input.max = this.options.max;
        input.value = this.options.defaultValue;
        L.DomEvent.disableClickPropagation(input);
        window.setInterval(() => {
            if (this.subscription) {
                this.subscription.request(Number.parseInt(input.value));
            }
        }, 1000);
        return input;
    },

    useSubscription(sub) {
        this.subscription = sub;
    },

    cancel() {
        if (this.subscription) {
            this.subscription.cancel();
            this.subscription = null;
        }
    }

});

class Radar {

    constructor(radar) {
        this.code = radar.code;
        this.name = radar.name;
        this.location = radar.location;
        this.marker = L.marker(
            [radar.location.lat, radar.location.lng],
            {title: this.code, icon: radarIcon}
        ).bindPopup(`${this.code} ${this.name}`);
    }

    addToLayer(layer) {
        this.marker.addTo(layer);
    }

    removeFromLayer(layer) {
        this.marker.removeFrom(layer);
    }

}

class AircraftSignal {

    constructor(signal) {
        this.callSign = signal.callSign;
        this.marker = L.marker([], {title: this.callSign, icon: planeIcon});
        this.update(signal)
    }

    update(signal) {
        this.bearing = signal.bearing;
        this.location = signal.location;
        this.marker.setRotationAngle(signal.bearing);
        this.marker.setLatLng([signal.location.lat, signal.location.lng]);
    }

    addToLayer(layer) {
        this.marker.addTo(layer);
    }

    removeFromLayer(layer) {
        this.marker.removeFrom(layer);
    }

}

class MapHandler {

    constructor(map) {
        this.map = map;
    }

    fireAndForget(payload) {
        if(typeof payload.metadata == Metadata  && payload.metadata.get(Metadata.ROUTE) == "send.to.location") {
            const radar = payload.data;
            this.map.panTo([radar.location.lat, radar.location.lng]);
        }
    }
    
}