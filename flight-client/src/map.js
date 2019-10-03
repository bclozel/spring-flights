import {TrackerClient} from './tracker';
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

    constructor(zoomLevel) {
        this.L = L;
        this.map = this.L.map('map');
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

        this.trackerClient = new TrackerClient('ws://localhost:8080/rsocket', new MapHandler(this.map));
        this.trackerClient.connect()
            .then(sub => {
                return this.trackerClient.fetchUserProfile();
            })
            .then(profile => {
                document.querySelector("#profile").innerHTML
                    = `<div class="navbar-item"><span>${profile.name}</span><img src="${profile.avatarUrl}"/></div>`;
                this.displayMap(profile, zoomLevel);
            });

        this.backpressureCtrl = new L.Control.BackpressureCtrl();
        this.backpressureCtrl.addTo(this.map);

        this.profileCtrl = new L.Control.ProfileCtrl(this.trackerClient);
        this.profileCtrl.addTo(this.map);
    }

    displayMap(profile, zoomLevel) {
        const radar = profile.airport;
        this.map.setView([radar.lat, radar.lng], zoomLevel);
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
        this.trackerClient
            .locateRadars(bounds.getNorthEast(), bounds.getSouthWest(), maxRadars)
            .then(data => {
                data.forEach(r => {
                    const radar = new Radar(r);
                    radar.addToLayer(this.radarsLayer);
                    this.radars.push(radar);
                });

                const radarRequest = this.radars.map(v => {
                    return {code: v.code};
                });
                this.trackerClient
                    .streamAircraftPositions(radarRequest)
                    .subscribe({
                        onSubscribe: sub => {
                            this.backpressureCtrl.useSubscription(sub);
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
                        onError: error => {
                            console.error(error);
                            console.dir(error);
                        }
                    });
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
        let field = L.DomUtil.create('div', "field");
        let control = L.DomUtil.create('div', "control", field);
        let input = L.DomUtil.create('input', "input is-large", control);
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
        return field;
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

/*
<div class="field has-addons">
  <div class="control">
    <input class="input" type="text" placeholder="Find a repository">
  </div>
  <div class="control">
    <a class="button is-info">
      Search
    </a>
  </div>
</div>
 */
L.Control.ProfileCtrl = L.Control.extend({
    options: {
        position: 'bottomright'
    },

    initialize: function (trackerClient) {
        this.trackerClient = trackerClient;
    },

    onAdd: function(map) {
        this.map = map;
        let field = L.DomUtil.create('div', "field has-addons");
        let controlInput = L.DomUtil.create('div', "control", field);
        this.input = L.DomUtil.create('input', "input", controlInput);
        this.input.type = 'text';
        this.input.placeholder = 'Find another user';
        L.DomEvent.disableClickPropagation(this.input);
        let controlButton = L.DomUtil.create('div', "control", field);
        this.button = L.DomUtil.create('a', "button is-info", controlButton);
        this.button.innerHTML = 'Search';
        L.DomEvent.disableClickPropagation(this.button);
        L.DomEvent.on(this.button, 'click', this.showUserOnMap, this);
        return field;
    },

    onRemove: function(map) {
        L.DomEvent.off(this.button, 'click', this.showUserOnMap, this);
        if (this.userMarker) {
            this.userMarker.removeFrom(map);
        }
    },

    showUserOnMap: function() {
        if (this.userMarker) {
            this.userMarker.removeFrom(this.map);
            this.userMarker = null;
        }
        if (!this.input.value) {
            return;
        }
        this.trackerClient.fetchPublicUserProfile(this.input.value)
            .then(profile => {
                let userIcon = L.icon({
                    iconSize: [64, 64], iconUrl: profile.avatarUrl, className: "profileIcon"
                });
                this.userMarker = L.marker(
                    [profile.airport.lat, profile.airport.lng],
                    {title: `${profile.name} @ ${profile.airport.code}`, icon: userIcon, zIndexOffset: 10}
                );
                this.userMarker.addTo(this.map);
                this.map.panTo([profile.airport.lat, profile.airport.lng]);
            });
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