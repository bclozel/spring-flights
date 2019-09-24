import {RadarMap} from './map';

const zoomLevel = 10;

window.onload = function init() {
    fetch('/profile/rstoyanchev').then(response => {
        response.json().then(profile => {
            const map = new RadarMap(profile.favoriteAirport, zoomLevel);
        });
    });
};


