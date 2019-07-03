import {RadarMap} from './map';

const startCoordinates = [45.757237, 4.832147];
const zoomLevel = 10;


window.onload = function init() {
    var map = new RadarMap(startCoordinates, zoomLevel);
};


