import 'bulma/css/bulma.min.css';
import {RadarMap} from './map';

const zoomLevel = 10;

window.onload = function init() {
    fetch('/profile/rstoyanchev').then(response => {
        response.json().then(profile => {
            document.querySelector("#profile").innerHTML
                = `<div class="navbar-item"><span>${profile.name}</span><img src="${profile.avatarUrl}"/></div>`;
            const map = new RadarMap(profile.favoriteAirport, zoomLevel);
        });
    });
};


