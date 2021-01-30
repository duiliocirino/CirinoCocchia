var lat = 0;
var long = 0;

function setPosition(position) {
    lat = position.coords.latitude;
    long = position.coords.longitude;
    initializeMap();
}

navigator.geolocation.getCurrentPosition(setPosition);


function initializeMap(){
    var mymap = L.map('mapid').setView([lat, long], 13);

    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox/streets-v11',
        tileSize: 512,
        zoomOffset: -1,
        accessToken: 'pk.eyJ1IjoiZHVpbGlvY2lyaW5vIiwiYSI6ImNra2dvazc0ZDE1bmoycXF0ZTk3aTBjcTgifQ.rgL0JFVT3tYfioUROB3TwQ'
    }).addTo(mymap);

    var groceries = [[${groceries}]];
    for(i = 0; i < groceries.length; i++){
        var marker = L.marker([groceries[i].latitude],[groceries[i].longitude]).addTo(mymap);
        marker.bindPopup(groceries[i].getName + "<br>");
    }

}