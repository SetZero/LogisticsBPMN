export async function getGeoCoding(country, postalcode, street) {
    let url = "https://nominatim.openstreetmap.org/search?country=" + country + "&postalcode=" + postalcode + "&street=" + street + "&format=json";
    return fetch(url);
}

export async function getReverseGeoCoding(lat, long) {
    let url = "https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + long + "&format=json";
    return fetch(url);
}

export function locateMe(writeTo) {

    navigator.geolocation.getCurrentPosition((loc) => {
        let long = loc.coords.longitude;
        let lat = loc.coords.latitude;
        writeTo(long + " " + lat);
        getReverseGeoCoding(lat, long).then(e => e.json()).then(e => {
            console.log(e);
        });
    });
}