import 'bulma/css/bulma.min.css';
import {RadarMap} from './map';

const zoomLevel = 10;

window.onload = function init() {
    const map = new RadarMap("rstoyanchev", zoomLevel);
};


