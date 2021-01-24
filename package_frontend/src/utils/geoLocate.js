let fetchHeaders = new Headers({
    "User-Agent"   : "LogisticsBPMN"
});

export async function getGeoCoding(country, postalcode, street) {
    let url = "https://nominatim.openstreetmap.org/search?country=" + country + "&postalcode=" + postalcode + "&street=" + street + "&format=json";
    return fetch(url, {headers: fetchHeaders});
}

export async function getReverseGeoCoding(lat, long) {
    let url = "https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + long + "&format=json";
    return fetch(url, {headers: fetchHeaders});
}

export async function getFreeGeoCoding(query) {
    console.log(query);
    let url = " https://nominatim.openstreetmap.org/search?q=" + query + "&country=germany&format=json";
    return fetch(url, {headers: fetchHeaders});
}

export function locateMe(writeTo) {

    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition((loc) => {
            let long = loc.coords.longitude;
            let lat = loc.coords.latitude;
            writeTo(long + " " + lat);
            getReverseGeoCoding(lat, long)
                .then(e => e.json())
                .then(e => {
                    resolve(e);
                })
                .catch(e => reject(e));
        }, (e) => reject(e));
    });
}

export async function findLocationByName(locationName, setter) {
    let result = await (await getFreeGeoCoding(locationName)).json();
    if(result.length > 0) {
        setter(result[0].display_name);
        return result[0].lon + " " + result[0].lat;
    }
    return "";
}